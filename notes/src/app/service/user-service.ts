import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { User } from "../resource/user";
import { catchError } from "rxjs";

@Injectable({ providedIn: 'root' })
export class UserService {

    private httpClient: HttpClient = inject(HttpClient);

    private readonly USERS_API = 'http://localhost:8080/api/users';
    private snackBar = inject(MatSnackBar);

    getUserByUsername(username: string) {
        return this.httpClient.get<any>(this.USERS_API + '?query=' + encodeURIComponent(`{"username":"${username}"}`)).pipe(
            catchError((error) => { this.snackBar.open(error, 'Dismiss', { duration: 3000 }); throw error; })
        );
    }

}