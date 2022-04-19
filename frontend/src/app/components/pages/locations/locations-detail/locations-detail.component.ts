import { Component, OnInit } from '@angular/core';
import {ArtistWithEvents} from "../../../../dtos/artist/artist-with-events";
import {ArtistService} from "../../../../services/artist.service";
import {ActivatedRoute} from "@angular/router";
import {LocationWithEvents} from "../../../../dtos/location/location-with-events";
import {LocationService} from "../../../../services/location.service";

@Component({
  selector: 'app-locations-detail',
  templateUrl: './locations-detail.component.html',
  styleUrls: ['./locations-detail.component.css']
})
export class LocationsDetailComponent implements OnInit {
  error = false;
  errorMessage = '';
  location_with_events: LocationWithEvents;
  id: any;

  constructor(private locationService: LocationService, private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.loadLocationWithEvents();
  }

  private loadLocationWithEvents() {
    this.locationService.getLocationWithEventById(this.id).subscribe(
      (location_with_events: LocationWithEvents) => {
        this.location_with_events = location_with_events;
        return this.location_with_events;
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
