import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {Location} from "../dtos/location/location";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from "../global/globals";
import {LocationQuery} from '../dtos/location/location-query';
import {LocationPage} from '../dtos/location/location-page';
import {ArtistWithEvents} from "../dtos/artist/artist-with-events";
import {LocationWithEvents} from "../dtos/location/location-with-events";
import {CreateLocation} from '../dtos/location/create-location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {


  private messageBaseUri: string = this.globals.backendUri + '/location/';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

   /**
   * Loads all locations with query from the backend
   */
  getLocation(locationQuery): Observable<LocationPage> {

    let params = new HttpParams();
    for(let key in locationQuery) {
      if(locationQuery[key] !== null) {
        params = params.set(key, locationQuery[key]);
      }
    }

     return this.httpClient.get<LocationPage>(this.messageBaseUri, {params: params});
  }

  /**
   * Loads location with event by id
   */
  getLocationWithEventById(id): Observable<LocationWithEvents> {
    return this.httpClient.get<LocationWithEvents>(this.messageBaseUri + id);
  }

  getAllLocations(): Observable<Location[]> {
    return this.httpClient.get<Location[]>(this.messageBaseUri + 'list');
  }

  createLocation(location: CreateLocation) {
    return this.httpClient.post(this.messageBaseUri, location, {responseType: 'json'});
  }
}
