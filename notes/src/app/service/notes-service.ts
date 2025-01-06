import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { Note } from "../resource/note";
import { MatSnackBar } from "@angular/material/snack-bar";
import { BehaviorSubject, catchError, finalize, Observable, Subject, tap } from "rxjs";
import { AuthService } from "./auth-service";

@Injectable({ providedIn: 'root' })
export class NoteService {

    private httpClient: HttpClient = inject(HttpClient);

    private authService: AuthService = inject(AuthService);

    notes = signal<Note[]>([]);
    totalElements = signal<number>(0);

    private readonly NOTES_API = 'http://localhost:8080/api/notes';
    private snackBar = inject(MatSnackBar);

    eventStream(id: string): Observable<Note | undefined> {
        const behaviorSubject = new Subject<Note | undefined>();
        let eventSource: EventSource | undefined;
        this.authService.getAccessToken().subscribe(token => {
            eventSource = new EventSource(`${this.NOTES_API}/${id}?access_token=${token}`);
            eventSource.onmessage = (event) => behaviorSubject.next(JSON.parse(event.data))
        });
        return behaviorSubject.pipe(finalize(() => eventSource?.close()));
    }

    refreshNotes(query?: string, page: number = 0, size: number = 5, sort: string = 'title,asc') {
        const q = `?page=${page}&size=${size}&sort=${sort}` + (query ? `&query=` + encodeURIComponent(`{
            $text: {
                $search: '${query}',
                $caseSensitive: false
            }
            }`) : '');
        return this.httpClient.get(this.NOTES_API + q).pipe(
            catchError((error) => { this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }); throw error; })).subscribe((notes: any) => {
                this.notes.set(notes.content);
                this.totalElements.set(notes.page.totalElements);
            });
    }

    getNote(id: string) {
        return this.httpClient.get<Note>(this.NOTES_API + '/' + id);
    }
    createOrUpdateNote(note: Note) {
        note.id ? this.httpClient.put(this.NOTES_API + '/' + note.id, note).pipe(
            catchError((error) => { this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }); throw error; })).subscribe(() => this.refreshNotes()) :
            this.httpClient.post(this.NOTES_API, note).pipe(
                catchError((error) => { this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }); throw error; })).subscribe(() => this.refreshNotes());
    }

    delete(id?: string) {
        if (id) {
            this.httpClient.delete(this.NOTES_API + '/' + id).pipe(
                catchError((error) => { this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }); throw error; })).subscribe(() => this.refreshNotes());
        }
    }

}