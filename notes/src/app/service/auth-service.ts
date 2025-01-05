import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { OidcSecurityService } from "angular-auth-oidc-client";
import { catchError, map, Observable, take } from "rxjs";
import { UserInfo } from "../auth/user-info";

@Injectable({ providedIn: 'root' })
export class AuthService {

    private httpClient: HttpClient = inject(HttpClient);
    private oidcSecurityService = inject(OidcSecurityService);
    private snackBar = inject(MatSnackBar);

    checkAuth() {
        if (localStorage.getItem('login') === 'github') {
            localStorage.setItem('login', '');
            this.oidcSecurityService.authorize();
        } else {
            this.oidcSecurityService.checkAuth().subscribe(authResponse => {
                if (authResponse.errorMessage) {
                    this.snackBar.open(authResponse.errorMessage, 'Dismiss', { duration: 3000 });
                }
            });
        }
    }

    isAuthenticated(): Observable<boolean> {
        return this.oidcSecurityService.isAuthenticated$.pipe(map(({ isAuthenticated }) => isAuthenticated));
    }

    login(username: string, password: string, tenant: string = '') {
        const body = new HttpParams()
            .set('username', username)
            .set('password', password)
        this.httpClient.post<string>('http://localhost:8080/login',
            body.toString(),
            {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-Tenant-Id': tenant },
            }
        ).pipe(
            take(1),
            catchError(() => {
                this.snackBar.open('Invalid username or password', 'Dismiss', { duration: 3000 });
                return [];
            }
            )
        ).subscribe(() => {
            localStorage.setItem('tenantId', tenant);
            this.oidcSecurityService.authorize();
        }
        );
    }

    githubLogin(tenant: string = '') {
        localStorage.setItem('login', "github");
        localStorage.setItem('tenantId', tenant);
        window.open('http://localhost:8080/oauth2/authorization/github?tenantId=' + tenant, '_self');
    }

    userInfo(): Observable<UserInfo> {
        return this.oidcSecurityService.getUserData();
    }

    logout() {
        this.oidcSecurityService
            .logoff()
            .subscribe((result) => sessionStorage.clear());
    }
}