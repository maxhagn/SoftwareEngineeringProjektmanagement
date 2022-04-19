import {Component, Input, OnInit} from '@angular/core';
import {Event} from "../../../dtos/event/event";

@Component({
  selector: 'app-events-entry',
  templateUrl: './events-entry.component.html',
  styleUrls: ['./events-entry.component.css']
})
export class EventsEntryComponent implements OnInit {

  @Input() entry: Event;

  constructor() { }

  ngOnInit(): void {
  }

}
