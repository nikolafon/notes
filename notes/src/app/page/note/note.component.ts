import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { NoteService } from '../../service/notes-service';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Note } from '../../resource/note';
import { UserService } from '../../service/user-service';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'notes',
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss'
})
export class NoteComponent implements OnInit {

  private noteService: NoteService = inject(NoteService);
  private userService: UserService = inject(UserService);
  private router: Router = inject(Router);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);
  note: Note = { id: undefined, title: '', content: '', collaborators: [] };

  form: FormGroup = new FormGroup({
    title: new FormControl(this.note.title),
    content: new FormControl(this.note.content),
    collaborator: new FormControl('')
  });

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.noteService.getNote(id).subscribe(note => {
          this.note = note;
          this.form.setValue({ title: note.title, content: note.content, collaborator: '' });
        });
      }
    });
  }

  addCollaborator(username: string) {
    this.userService.getUserByUsername(username).subscribe(response => {
      if (response.page.totalElements > 0) {
        this.note.collaborators.push(username);
        this.form.get('collaborator')?.setValue('');
      } else {
        this.snackBar.open('User not found', 'Dismiss', { duration: 3000 });
      }
    });
  }

  removeCollaborator(username: string) {
    this.note.collaborators = this.note.collaborators.filter(collaborator => collaborator !== username);
  }

  submit() {
    if (this.form.valid) {
      this.noteService.createOrUpdateNote({ ... this.note, ... this.form.value });
      this.router.navigate(['/notes']);
    };
  }

  back() {
    this.router.navigate(['/notes']);
  }

}

