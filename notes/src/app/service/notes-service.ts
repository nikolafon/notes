import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { Note } from "../resource/note";
import { MatSnackBar } from "@angular/material/snack-bar";
import { catchError } from "rxjs";

@Injectable({ providedIn: 'root' })
export class NoteService {

    private httpClient: HttpClient = inject(HttpClient);

    notes = signal<Note[]>([]);
    totalElements = signal<number>(0);

    private readonly NOTES_API = 'http://localhost:8080/api/notes';
    private snackBar = inject(MatSnackBar);

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