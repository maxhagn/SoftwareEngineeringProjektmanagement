import { Component, OnInit } from '@angular/core';
import {Role} from '../../../../global/role';
import {AuthService} from '../../../../services/auth.service';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../../../global/globals';
import {EventService} from '../../../../services/event.service';
import {EventDto} from '../../../../dtos/event-dto';
import {EventPage} from '../../../../dtos/event/event-page';
import {EventQuery} from '../../../../dtos/event/event-query';


@Component({
  selector: 'app-events-view',
  templateUrl: './events-view.component.html',
  styleUrls: ['./events-view.component.css']
})
export class EventsViewComponent implements OnInit {

  /*Role*/
  role = Role.ADMIN;

  /*errors*/
  error: boolean = false;
  uploadError: boolean = false;
  errorMessage: string = '';
  extendedSearchFlag: boolean;
  searchButtonText: string;

  events: EventDto[];

  page = new EventPage();
  eventQuery = new EventQuery();


  /*PICTURE*/
  public files: any[];
  retrieveResonse: any;
  retrievedImage: any;
  base64Data: any;
  map;

  // tslint:disable-next-line:max-line-length
  constructor(public authService: AuthService, public eventService: EventService, private httpClient: HttpClient, private globals: Globals) {
    this.eventQuery.page = 0;
    this.page.content = [];
    this.eventQuery.type = "ALL";
    this.eventQuery.order = "asc";
    this.eventQuery.sortedBy = "title";
  }

  ngOnInit(): void {
    this.map = new Map();
   // this.getEvents();
    this.eventQuery.title = '';
    this.extendedSearchFlag = false;
    this.searchButtonText = 'Extended Search';
    this.loadEvents();
  }

  public changeFlag() {
    // tslint:disable-next-line:triple-equals
    if (this.extendedSearchFlag == true) {
      this.extendedSearchFlag = false;
      this.searchButtonText = 'Extended Search';
    } else{
      this.extendedSearchFlag = true;
      this.eventQuery.title = '';
      this.searchButtonText = 'Mixed Search';
    }
  }

  public loadDetailedEvents() {
    this.eventQuery.detailed = true;
    this.loadEvents();

  }

  public loadMixedEvents() {
    this.eventQuery.detailed = false;
    this.loadEvents();

  }


  private getEvents() {
    this.eventService.getEvents().subscribe((events: EventDto[]) => {
        this.events = events;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });

  }


  /**
   * Loads all events from the database
   * @throws error 404 not found exception
   */
  private loadEvents() {
      this.eventService.getEvent(this.eventQuery).subscribe(
      (eventPage: EventPage) => {
        this.page = eventPage;
        if ( this.page.totalElements < 12 ) { this.eventQuery.page = 0; }
        return this.page;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private previousPage() {
    if ( this.eventQuery.page > 0 ) {
      this.eventQuery.page--;
    }

  }

  private nextPage() {
    if ( this.eventQuery.page >= 0 ) {
      this.eventQuery.page++;
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
