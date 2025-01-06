import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { isAuthenticated } from './auth/auth.guard';
import { LoginComponent } from './page/login/login.component';
import { NoteComponent } from './page/note/note.component';
import { NotesComponent } from './page/notes/notes.component';

export const routes: Routes = [{
    path: '',
    component: AppComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'notes',
    component: NotesComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'note',
    component: NoteComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'note/:id',
    component: NoteComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'login',
    component: LoginComponent,
}];
