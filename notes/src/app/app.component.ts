import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './service/auth-service';
import { CommonModule } from '@angular/common';
import { MaterialModule } from './material.module';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, MatButton],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'notes';
  authService: AuthService = inject(AuthService);
  ngOnInit() {
    this.authService.checkAuth();
  }
}
