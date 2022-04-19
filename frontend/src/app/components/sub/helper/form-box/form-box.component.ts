import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-form-box',
  templateUrl: './form-box.component.html',
  styleUrls: ['./form-box.component.css']
})
export class FormBoxComponent implements OnInit {

  @Input('back-link')
  public backLink: string = null;

  @Input('form-title')
  public formTitle: string = '';

  @Output('on-submit')
  public onSubmit = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void {
  }

}
