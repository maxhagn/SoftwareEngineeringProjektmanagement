import { Component, OnInit } from '@angular/core';
import {Artist} from '../../../../dtos/artist/artist';
import {ArtistService} from '../../../../services/artist.service';
import {ArtistQuery} from '../../../../dtos/artist/artist-query';
import {ArtistPage} from '../../../../dtos/artist/artist-page';
import {LocationPage} from '../../../../dtos/location/location-page';

@Component({
  selector: 'app-artists-view',
  templateUrl: './artists-view.component.html',
  styleUrls: ['./artists-view.component.css']
})
export class ArtistsViewComponent implements OnInit {
  page = new ArtistPage();
  error = false;
  errorMessage = '';
  artistQuery = new ArtistQuery();

  constructor(private artistService: ArtistService) {
    this.artistQuery.page = 0;
    this.page.content = [];
  }

  ngOnInit(): void {
    this.loadArtists();
  }

  /**
   * Loads all artists from the database
   * @throws error 404 not found exception
   */
  private loadArtists() {
    this.artistService.getArtist(this.artistQuery).subscribe(
      (artistPage: ArtistPage) => {
        this.page = artistPage;
        if ( this.page.totalElements < 12 ) { this.artistQuery.page = 0; }
        return this.page;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private previousPage() {
    if ( this.artistQuery.page > 0 ) {
      this.artistQuery.page--;
    }

  }

  private nextPage() {
    if ( this.artistQuery.page >= 0 ) {
      this.artistQuery.page++;
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
    console.log(error);
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
