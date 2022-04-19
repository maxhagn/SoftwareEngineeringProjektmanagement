import {CreateHall, CreateLocation, CreatePriceCategory} from '../dtos/location/create-location';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocationPersistanceService {

  private location: CreateLocation = new CreateLocation('', '', '', '', '', [], []);

  loadLocation() {
    return this.location;
  }

  saveLocation(location) {
    this.location = location;
  }

  addHall(hall: CreateHall) {
    this.location.halls.push(hall);
  }

  replaceLastHall(hall: CreateHall) {
    this.location.halls[this.location.halls.length - 1] = hall;
  }

  clear() {
    this.location = new CreateLocation('', '', '', '', '', [], []);
  }
}
