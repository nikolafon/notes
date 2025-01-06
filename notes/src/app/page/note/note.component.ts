import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { NoteService } from '../../service/notes-service';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Note } from '../../resource/note';
import { UserService } from '../../service/user-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, debounceTime, Subject } from 'rxjs';
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
  private save$ = new Subject<any>();
  private changeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.noteService.getNote(id).subscribe(note => {
          this.note = note;
          this.changeDetectorRef.detectChanges();
        });
        this.noteService.eventStream(id).subscribe(note => {
          if (note) {
            this.note = note;
            this.changeDetectorRef.detectChanges();
          }
        });
      }
    });
    this.save$.pipe(debounceTime(300)).subscribe(() => {
      if (this.note.title?.trim()) {
        this.noteService.createOrUpdateNote(this.note);
      }
    });
  }

  addCollaborator(username: string) {
    this.userService.getUserByUsername(username).subscribe(response => {
      if (response.page.totalElements > 0) {
        this.note.collaborators.push(username);
        this.save$.next({});
      } else {
        this.snackBar.open('User not found', 'Dismiss', { duration: 3000 });
      }
    });
  }

  removeCollaborator(username: string) {
    this.note.collaborators = this.note.collaborators.filter(collaborator => collaborator !== username);
    this.save$.next({});
  }

  titleChange(value: string) {
    this.note.title = value;
    this.save$.next({});
  }
  contentChange(value: string) {
    this.note.content = value;
    this.save$.next({});
  }

  back() {
    this.router.navigate(['/notes']);
  }

}

