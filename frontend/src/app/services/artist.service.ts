import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {ArtistQuery} from '../dtos/artist/artist-query';
import {Artist} from '../dtos/artist/artist';
import {ArtistPage} from '../dtos/artist/artist-page';
import {ArtistWithEvents} from "../dtos/artist/artist-with-events";

@Injectable({
  providedIn: 'root'
})
export class ArtistService {


  private messageBaseUri: string = this.globals.backendUri + '/artists/';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all artists with query from the backend
   */
  getArtist(artistQuery): Observable<ArtistPage> {

    let params = new HttpParams();
    for(let key in artistQuery) {
      if(artistQuery[key] !== null) {
        params = params.set(key, artistQuery[key]);
      }
    }
    return this.httpClient.get<ArtistPage>(this.messageBaseUri, {params: params});
  }

  /**
   * Loads artist with event by id
   */
  getArtistWithEventById(id): Observable<ArtistWithEvents> {
    return this.httpClient.get<ArtistWithEvents>(this.messageBaseUri + id);
  }

  /**
   * Loads all artists
   */
  getAllArtists(): Observable<Artist[]> {
    return this.httpClient.get<Artist[]>(this.messageBaseUri + 'list');
  }
}
