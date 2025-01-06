import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MaterialModule } from '../../material.module';
import { AuthService } from '../../service/auth-service';
import { NoteService } from '../../service/notes-service';
import { BehaviorSubject, debounceTime, Subject } from 'rxjs';
import { Sort } from '@angular/material/sort';
import { PageEvent } from '@angular/material/paginator';
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
  totalElements = this.noteService.totalElements;
  displayedColumns: string[] = ['title', 'owner', 'action'];
  searchTerm: string | undefined = '';
  sort: Sort = { active: '', direction: '' };
  page: number = 0;
  size: number = 5;
  private filter$ = new BehaviorSubject<any>({});

  ngOnInit() {
    this.filter$.pipe(debounceTime(300)).subscribe(() =>
      this.noteService.refreshNotes(this.searchTerm, this.page, this.size, this.sort.active + ',' + this.sort.direction));
  }

  delete(id?: string) {
    this.noteService.delete(id);
  }

  search(searchTerm?: string) {
    this.searchTerm = searchTerm;
    this.filter$.next({});
  }

  pageTable(event: PageEvent) {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.search();
  }

  sortTable(sort: Sort) {
    this.sort = sort;
    this.search();
  }

}

