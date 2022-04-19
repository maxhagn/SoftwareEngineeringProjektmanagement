import { Component, OnInit } from '@angular/core';
import { Location } from '../../../../dtos/location/location';
import { LocationQuery } from '../../../../dtos/location/location-query';
import {LocationService} from '../../../../services/location.service';
import {LocationPage} from '../../../../dtos/location/location-page';
import {Role} from '../../../../global/role';
import {AuthService} from '../../../../services/auth.service';

@Component({
  selector: 'app-locations-view',
  templateUrl: './locations-view.component.html',
  styleUrls: ['./locations-view.component.css']
})
export class LocationsViewComponent implements OnInit {
  page = new LocationPage();
  error = false;
  errorMessage = '';
  locationQuery = new LocationQuery();
  extendedSearchFlag: boolean;
  extendedSearchButtonText: string;
  role = Role.ADMIN;


  constructor(public authService: AuthService,private locationService: LocationService) {
    this.locationQuery.page = 0;
    this.locationQuery.order = "asc";
    this.locationQuery.sortedBy = "name";
    this.extendedSearchFlag = false;
    this.extendedSearchButtonText = "Extended Search";
    this.page.content = [];
  }

  ngOnInit(): void {
    this.loadLocations();
  }

  /**
   * Loads all locations with query from the database
   * @throws error 404 not found exception
   */
  private loadLocations() {
    this.locationService.getLocation(this.locationQuery).subscribe(
      (locationPage: LocationPage) => {
        this.page = locationPage;
        if ( this.page.totalElements < 12 ) { this.locationQuery.page = 0; }
        return this.page;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  private previousPage() {
    if ( this.locationQuery.page > 0 ) {
      this.locationQuery.page--;
    }

  }

  private nextPage() {
    if ( this.locationQuery.page >= 0 ) {
      this.locationQuery.page++;
    }
  }

  private setSearchLevel() {
    if ( this.extendedSearchFlag ) {
      this.extendedSearchFlag = false;
      this.locationQuery.name = null;
      this.locationQuery.street = null;
      this.locationQuery.city = null;
      this.locationQuery.plz = null;
      this.locationQuery.order = "asc";
      this.locationQuery.sortedBy = "name";
      this.extendedSearchButtonText = "Extended Search";
      this.loadLocations();
    } else {
      this.extendedSearchFlag = true;
      this.locationQuery.mixedQuery = null;
      this.extendedSearchButtonText = "Mixed Search";
      this.loadLocations();
    }
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
