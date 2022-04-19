import {Component, Input, OnInit} from '@angular/core';
import {Artist} from '../../../dtos/artist/artist';

@Component({
  selector: 'app-artists-entry',
  templateUrl: './artists-entry.component.html',
  styleUrls: ['./artists-entry.component.css']
})
export class ArtistsEntryComponent implements OnInit {

  @Input() entry: Artist;

  constructor() { }

  ngOnInit(): void {
  }

}
