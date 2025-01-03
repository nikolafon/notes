import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { NoteService } from '../../service/notes-service';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Note } from '../../resource/note';
@Component({
  selector: 'notes',
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss'
})
export class NoteComponent implements OnInit {

  noteService: NoteService = inject(NoteService);
  private router: Router = inject(Router);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  note: Note = { id: undefined, title: '', content: '' };

  form: FormGroup = new FormGroup({
    title: new FormControl(this.note.title),
    content: new FormControl(this.note.content)
  });

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.noteService.getNote(id).subscribe(note => {
          this.note = note;
          this.form.setValue({ title: note.title, content: note.content }); 
        });
      }
    });
  }

  submit() {
    if (this.form.valid) {
      this.noteService.createOrUpdateNote({... this.note,... this.form.value});
      this.router.navigate(['/notes']);
    };
  }

  back() {
    this.router.navigate (['/notes']);
  }


}

