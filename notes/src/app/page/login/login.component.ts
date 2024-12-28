import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaterialModule } from '../../material.module';
import { AuthService } from '../../service/auth-service';
@Component({
  selector: 'login',
  imports: [MaterialModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  private authService: AuthService = inject(AuthService);

  form: FormGroup = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    tenant: new FormControl(''),
  });

  submit() {
    if (this.form.valid) {
      this.authService.login(this.form.get('username')?.value, this.form.get('password')?.value,this.form.get('tenant')?.value);
    };
  }

  githubLogin() {
    this.authService.githubLogin(this.form.get('tenant')?.value);
  }

}

