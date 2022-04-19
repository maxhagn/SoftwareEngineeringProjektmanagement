import { Component, OnInit } from '@angular/core';
import {EventDto} from "../../../../dtos/event-dto";
import {Artist} from "../../../../dtos/artist/artist";
import {ArtistService} from "../../../../services/artist.service";
import {ActivatedRoute} from "@angular/router";
import {ArtistPage} from "../../../../dtos/artist/artist-page";
import {ArtistWithEvents} from "../../../../dtos/artist/artist-with-events";
import {ArtistQuery} from "../../../../dtos/artist/artist-query";

@Component({
  selector: 'app-artists-detail',
  templateUrl: './artists-detail.component.html',
  styleUrls: ['./artists-detail.component.css']
})
export class ArtistsDetailComponent implements OnInit {
  error = false;
  errorMessage = '';
  artist_with_events: ArtistWithEvents;
  id: any;

  constructor(private artistService: ArtistService, private route: ActivatedRoute) {

  }

  ngOnInit(): void {
     this.id = this.route.snapshot.paramMap.get('id');
     this.loadArtistsWithEvents()
  }

  private loadArtistsWithEvents() {
    this.artistService.getArtistWithEventById(this.id).subscribe(
      (artist_with_events: ArtistWithEvents) => {
        this.artist_with_events = artist_with_events;
        return this.artist_with_events;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false; // set error flag to false
  }

  /**
   * Checking the Error Status to inform user about error
   * @param error the error from the backend
   */
  private defaultServiceErrorHandling(error: any) {
    this.error = true;
    if (error.status === 0) {
      // If status is 0, the backend is probably down
      this.errorMessage = 'The backend seems not to be reachable';
    } else if ( error.status === 404 )  {
      // If status 404, the object could not be found in the database
      this.errorMessage = 'There where no entries in the database to your query';
    }  else if ( error.status === 422 )  {
      // If status 422, the validation failed
      this.errorMessage = 'There values you entered are not supported, check the requirements';
    } else if (error.error.message === 'No message available') {
      // If no detailed error message is provided, fall back to the simple error name
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error.message;
    }
  }
}
