import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.css']
})
export class SearchBoxComponent implements OnInit {

  @Input('search-button-text')
  public searchButtonText: string = '';

  @Output('submit')
  public submit: EventEmitter<void> = new EventEmitter<void>();

  @Output('onClick')
  public onClick:  EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void {
  }

}
