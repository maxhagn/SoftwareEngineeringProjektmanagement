import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-error-box',
  templateUrl: './error-box.component.html',
  styleUrls: ['./error-box.component.css']
})
export class ErrorBoxComponent implements OnInit, OnChanges {

  @Input('error-message')
  public errorMessage: string = '';

  @Output('error-messageChange')
  public errorMessageChange: EventEmitter<string> = new EventEmitter<string>();

  @Input('disable-auto-vanish')
  public autoVanishDisabled: Boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.errorMessage && changes.errorMessage.currentValue != changes.errorMessage.previousValue && changes.errorMessage.currentValue != '') {
      if (!this.autoVanishDisabled) {
        setTimeout(_ => {
          this.vanishError();
        }, 5000);
      }
    }
  }

  /**
   * When error is set to empty string it disapears
   */
  vanishError() {
    this.errorMessage = '';
    this.errorMessageChange.emit('');
  }

}
