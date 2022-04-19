import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {catchError} from 'rxjs/operators';
import {EventDto} from '../dtos/event-dto';
import {EventPage} from '../dtos/event/event-page';
import {TopTenQuery} from '../dtos/event/top-ten-query';
import {TopTenDto} from '../dtos/event/top-ten-dto';


@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventsBaseUri: string = this.globals.backendUri + '/events';

  public errorMessage: string = '';


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all events from the backend
   */
  getEvents(): Observable<EventDto[]> {
    return this.httpClient.get<EventDto[]>(this.eventsBaseUri + '/list');
  }

  /**
   * create an event
   * @param event to be created
   */
  createEvent(events: EventDto): Observable<EventDto> {
    return this.httpClient.post<EventDto>(this.eventsBaseUri + '/', events)
      .pipe(
        catchError((err) => {
          return throwError(err);    /*Rethrow it back to component*/
        }));
  }

  /**
   * finds an event specified by id
   * @param id to be found
   */
  find(id: number): Observable<EventDto> {
    return this.httpClient.get<EventDto>(this.eventsBaseUri + '/' + id);
  }

  /**
   * Loads all events with query from the backend
   */
  getEvent(eventQuery): Observable<EventPage> {

    let params = new HttpParams();
    for (const key in eventQuery) {
      if (eventQuery[key] !== null) {
        params = params.set(key, eventQuery[key]);
      }
    }

    return this.httpClient.get<EventPage>(this.eventsBaseUri + '/', {params: params});
  }

  /**
   * Loads top ten  events
   * @param topTenQuery contains category and date
   */
  getTop(topTenQuery: TopTenQuery) {
    let params = new HttpParams();
    for (const key in topTenQuery) {
      if (topTenQuery[key] !== null) {
        params = params.set(key, topTenQuery[key]);
      }
    }
    return this.httpClient.get<TopTenDto[]>(this.eventsBaseUri + '/top/', {params: params});
  }
}
