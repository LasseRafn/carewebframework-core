import { Component } from '@angular/core';

declare var System;

// Pomodoro timer component
@Component({
  template: `
    <div class="text-center">
      <img src="{{zk}}org/carewebframework/ui/angular/example/assets/img/pomodoro.png" alt="Pomodoro">
      <h1> {{ minutes }}:{{ seconds | number: '2.0' }} </h1>
      <p>
        <button (click)="togglePause()"
          class="btn btn-danger">
          {{ buttonLabel }}
        </button>
      </p>
    </div>
`
})
export class AngularComponent {
  minutes: number;
  seconds: number;
  isPaused: boolean;
  buttonLabel: string;
  zk: string = System.paths['zk:'];

  constructor() {
    this.resetPomodoro();
    setInterval(() => this.tick(), 1000);
  }

  resetPomodoro(): void {
    this.isPaused = true;
    this.minutes = 24;
    this.seconds = 59;
    this.buttonLabel = 'Start';
  }

  private tick(): void {
    if (!this.isPaused) {
      this.buttonLabel = 'Pause';

      if (--this.seconds < 0) {
        this.seconds = 59;
        if (--this.minutes < 0) {
          this.resetPomodoro();
        }
      }
    }
  }

  togglePause(): void {
    this.isPaused = !this.isPaused;
    if (this.minutes < 24 || this.seconds < 59) {
      this.buttonLabel = this.isPaused ? 'Resume' : 'Pause';
    }
  }
}

