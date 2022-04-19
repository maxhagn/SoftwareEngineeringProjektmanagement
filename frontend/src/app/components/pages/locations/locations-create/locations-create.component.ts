import {Component, OnInit} from '@angular/core';
import {LocationPersistanceService} from '../../../../services/location-persistance.service';
import {Router} from '@angular/router';
import {CreateHall, CreateLocation, CreatePriceCategory} from '../../../../dtos/location/create-location';
import {LocationService} from '../../../../services/location.service';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-locations-create',
  templateUrl: './locations-create.component.html',
  styleUrls: ['./locations-create.component.css']
})
export class LocationsCreateComponent implements OnInit {

  public location: CreateLocation;
  public catName = '';
  public catPrice = 0;

  public hallName = '';
  public hallCols = 0;
  public hallRows = 0;

  public catErrors = {};
  public hallErrors = {};
  public locErrors = {};
  public errorMessage: string = '';

  constructor(private locationPersistanceService: LocationPersistanceService,
              private router: Router, private  locationService: LocationService, private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.location = this.locationPersistanceService.loadLocation();
  }

  addHall() {
    this.hallErrors = {};
    if (this.location.categories.length <= 0) {
      this.hallErrors['cat'] = true;
    }
    if (this.hallName.length <= 0) {
      this.hallErrors['name'] = true;
    }
    if (this.hallCols <= 0) {
      this.hallErrors['cols'] = true;
    }
    if (this.hallRows <= 0) {
      this.hallErrors['rows'] = true;
    }

    if (Object.keys(this.hallErrors).length !== 0) {
      return;
    }

    this.locationPersistanceService.addHall(new CreateHall(this.hallName, this.hallCols, this.hallRows, []));
    this.locationPersistanceService.saveLocation(this.location);
    this.router.navigate(['/hall/create']);
  }

  addCategory() {
    this.catErrors = {};
    if (this.catPrice <= 0) {
      this.catErrors['price'] = true;
    }
    if (this.catName === '') {
      this.catErrors['name'] = true;
    }
    if (Object.keys(this.catErrors).length !== 0) {
      return;
    }
    this.location.categories.push(new CreatePriceCategory(this.catName, this.location.categories.length + 1, this.catPrice));
    this.catPrice = 0;
    this.catName = '';
  }

  submit() {
    this.catErrors = {};
    this.locErrors = {};
    this.hallErrors = {};

    if (this.location.name === '') {
      this.locErrors['name'] = true;
    }

    if (this.location.categories.length === 0) {
      this.locErrors['cat'] = true;
    }

    if (this.location.halls.length === 0) {
      this.locErrors['halls'] = true;
    }

    if (this.location.area_code === '') {
      this.locErrors['code'] = true;
    }

    if (this.location.city === '') {
      this.locErrors['city'] = true;
    }

    if (this.location.country === '') {
      this.locErrors['country'] = true;
    }

    if (this.location.street === '') {
      this.locErrors['street'] = true;
    }


    if (Object.keys(this.locErrors).length !== 0) {
      return;
    }

    this.locationService.createLocation(this.location).subscribe(data => {
        this.messageService.publishMessage('Successfuly created Location');
        this.locationPersistanceService.clear();
        this.router.navigate(['/locations']);
      },
      error => {
        this.errorMessage = error.error.error;
        window.scroll(0, 0);
      });
  }

  removeCategory(index: number) {
    this.location.categories.splice(index, 1);
  }

  removeHall(index: number) {
    this.location.halls.splice(index, 1);
  }

  categoryInUse(category: CreatePriceCategory): boolean {
    let ret = false;

    this.location.halls.forEach(h => {
      h.areas.forEach(a => {
        if (a.tmpPriceCategoryId === category.tmpId) {
          ret = true;
        }
      });
    });

    return ret;
  }
}
