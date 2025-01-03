import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { Note } from "../resource/note";
import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({ providedIn: 'root' })
export class NoteService {

    private httpClient: HttpClient = inject(HttpClient);

    notes = signal<Note[]>([]);

    private readonly NOTES_API = 'http://localhost:8080/api/notes';
    private snackBar = inject(MatSnackBar);

    refreshNotes() {
        return this.httpClient.get(this.NOTES_API).subscribe((notes: any) => this.notes.set(notes.content), (error) => this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }));
    }

    getNote(id: string) {
        return this.httpClient.get<Note>(this.NOTES_API + '/' + id);
    }
    createOrUpdateNote(note: Note) {
        note.id ? this.httpClient.put(this.NOTES_API + '/' + note.id, note).subscribe(() => this.refreshNotes()) :
            this.httpClient.post(this.NOTES_API, note).subscribe(() => this.refreshNotes(), (error) => this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }));
    }

    delete(id?: string) {
        if (id) {
            this.httpClient.delete(this.NOTES_API + '/' + id).subscribe(() => this.refreshNotes(), (error) => this.snackBar.open(error.error.message, 'Dismiss', { duration: 3000 }));
        }
    }

}