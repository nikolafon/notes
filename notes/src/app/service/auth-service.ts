import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { LoginResponse, OidcSecurityService } from "angular-auth-oidc-client";
import { catchError, map, Observable, take, tap } from "rxjs";
import { UserInfo } from "../auth/user-info";

@Injectable({ providedIn: 'root' })
export class AuthService {

    private httpClient: HttpClient = inject(HttpClient);
    private oidcSecurityService = inject(OidcSecurityService);
    private snackBar = inject(MatSnackBar);

    checkAuth() {
        this.oidcSecurityService
            .checkAuth().pipe(take(1))
            .subscribe();
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
            this.oidcSecurityService.authorize()
            localStorage.setItem('tenantId', tenant);
        }
        );
    }

    userInfo(): Observable<UserInfo> {
        return this.oidcSecurityService.getUserData();
    }

    logout() {
        this.oidcSecurityService
            .logoff()
            .subscribe((result) => console.log(result));
    }
}