import {AbstractControl, ValidationErrors} from '@angular/forms';
import {Performance} from './performance/performance';
import {Location} from './location/location';
export class Artist {
  constructor(public data) {}
}

class EventImages {
}

export class EventDto {
  constructor(
    public id: number,
    public title: string,
    public description: string,
    public category:  string,
    public duration: (string | ((control: AbstractControl) => (ValidationErrors | null))[])[],
    public artist: Artist,
    public performances: Performance[],
    public location: Location,
    public eventImages: EventImages[]
  ) {
  }
}
