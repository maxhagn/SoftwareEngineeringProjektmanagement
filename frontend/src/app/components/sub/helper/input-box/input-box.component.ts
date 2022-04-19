import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-input-box',
  templateUrl: './input-box.component.html',
  styleUrls: ['./input-box.component.css']
})
export class InputBoxComponent implements OnInit {

  @Input('label-for')
  public labelFor: string = '';

  @Input('label')
  public label: string = '';

  constructor() { }

  ngOnInit(): void {
  }

}
