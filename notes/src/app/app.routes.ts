import { Routes } from '@angular/router';
import { isAuthenticated } from './auth/auth.guard';
import { LoginComponent } from './page/login/login.component';
import { AppComponent } from './app.component';
import { HomeComponent } from './page/home/home.component';

export const routes: Routes = [{
    path: '',
    component: AppComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'home',
    component: HomeComponent,
    canActivate: [isAuthenticated]
},
{
    path: 'login',
    component: LoginComponent,
}];
