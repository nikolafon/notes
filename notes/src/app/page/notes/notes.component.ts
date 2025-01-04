import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { AuthService } from '../../service/auth-service';
import { NoteService } from '../../service/notes-service';
@Component({
  selector: 'notes',
  imports: [MaterialModule, CommonModule],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.scss'
})
export class NotesComponent implements OnInit {
  noteService: NoteService = inject(NoteService);
  authService: AuthService = inject(AuthService);
  notes = this.noteService.notes;
  displayedColumns: string[] = ['title','owner','action'];
  ngOnInit() {
    this.noteService.refreshNotes();
  }

  delete(id?: string) {
    this.noteService.delete(id);
  }

  filter(titleFilter: string) {
    this.noteService.refreshNotes(titleFilter);
  }

}

