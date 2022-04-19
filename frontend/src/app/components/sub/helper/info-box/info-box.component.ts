import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-info-box',
  templateUrl: './info-box.component.html',
  styleUrls: ['./info-box.component.css']
})
export class InfoBoxComponent implements OnInit, OnChanges {

  @Input('info-message')
  public infoMessage: string = '';

  @Output('info-messageChange')
  public infoMessageChange: EventEmitter<string> = new EventEmitter<string>();

  @Input('disable-auto-vanish')
  public autoVanishDisabled: Boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.infoMessage && changes.infoMessage.currentValue !==
      changes.infoMessage.previousValue && changes.infoMessage.currentValue !== '') {
      if (!this.autoVanishDisabled) {
        setTimeout(_ => {
          this.vanishInfo();
        }, 5000);
      }
    }
  }

  /**
   * When info is set to empty string it disappears
   */
  vanishInfo() {
    this.infoMessage = '';
    this.infoMessageChange.emit('');
  }

}
