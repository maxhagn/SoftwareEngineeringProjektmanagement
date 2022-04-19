import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {HelperUtils} from '../../../../global/helper-utils.service';
import {EventService} from '../../../../services/event.service';
import {EventDto} from '../../../../dtos/event-dto';
import {ArtistService} from '../../../../services/artist.service';
import {Artist} from '../../../../dtos/artist/artist';
import {Location} from '../../../../dtos/location/location';
import {LocationService} from '../../../../services/location.service';
import {HallDto} from '../../../../dtos/event/hallDto';
import {Performance} from '../../../../dtos/performance/performance';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-events-create',
  templateUrl: './events-create.component.html',
  styleUrls: ['./events-create.component.css']
})
export class EventsCreateComponent implements OnInit {

  // Wether form was submit at least one time
  public submitted: boolean = false;
  // Form used to validate inputs and retrive data
  public eventForm: FormGroup;
  public performanceForm: FormGroup;

  // As soon as this is set to an error an error dialog is shown
  public errorMessage: string = '';

  // All artists
  public artists: Artist[];
  public error: any;
  public locations: Location[];
  public selectedValue: any;
  public halls: HallDto[];
  public performances: Performance[] = [];
  private events: EventDto[];
  infoMessage: string = '';
  performanceError: string;

  constructor(private formBuilder: FormBuilder, private eventService:
    // tslint:disable-next-line:max-line-length
    EventService, private router: Router, private helperUtils: HelperUtils, private artistService: ArtistService, private locationService: LocationService, private messageService: MessageService) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      category: ['', [Validators.required]],
      duration: ['', [Validators.required]],
      artist: ['', [Validators.required]],
      location: ['', [Validators.required]],
      datetime: [''],
      hall: [''],
      cut: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.getArtists();
    this.getLocations();
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the home page
   * @param data recieved from the form
   */
  public onSubmit(data: any) {
    if (this.performances.length < 1) {
      this.errorMessage = 'You must create at least one performance for this event!';
      window.scroll(0, 0);
      return;
    }
    this.submitted = true;
    if (this.eventForm.valid) {
      const cut = this.eventForm.controls.cut.value;
      this.performances.forEach(p => p.min_price = cut);
      const eventDto: EventDto =
        new EventDto(null,
          this.eventForm.controls.title.value,
          this.eventForm.controls.description.value,
          this.eventForm.controls.category.value,
          this.eventForm.controls.duration.value,
          this.eventForm.controls.artist.value,
          this.performances,
          this.eventForm.controls.location.value,
          null);
      this.eventService.createEvent(eventDto).subscribe(
        () => {
          // this.router.navigate(['/events']);
          this.messageService.publishMessage('Event \'' + this.eventForm.controls.title.value + '\' created successfully');
          window.scroll(0, 0);
          this.eventForm.reset();
          this.submitted = false;
          this.halls = [];
          this.performances = [];
        },
        (error: any) => {
          this.errorMessage = this.helperUtils.getErrorText(error);
          window.scroll(0, 0);
        }
      );
    }
  }

  private getArtists() {
    this.artistService.getAllArtists().subscribe(
      artists => {
        this.artists = artists;
      },
      error => {
        this.error = error.error.error;
      }
    );
  }

  private getLocations() {
    this.locationService.getAllLocations().subscribe(
      locations => {
        this.locations = locations;
      },
      error => {
        this.error = error.error.error;
      }
    );
  }

  onChange() {

  }

  public updateHalls() {
    for (const location of this.locations) {
      if (location.id === this.eventForm.controls.location.value.id) {
        this.halls = location.halls;
      }
    }
    this.performances = [];
  }

  public addToPerfomances() {
    const tmp_performance: Performance = new Performance();
    tmp_performance.date = this.eventForm.controls.datetime.value;
    tmp_performance.hall = this.eventForm.controls.hall.value;
    if (tmp_performance.date.toString().length < 1 || tmp_performance.hall.id == null) {
      this.performanceError = 'You must set all fields for a performance';
      return;
    }
    this.performanceError = null;
    this.performances.push(tmp_performance);
  }

  public removePerformance(performance: Performance) {
    const index: number = this.performances.indexOf(performance);
    if (index !== -1) {
      this.performances.splice(index, 1);
    }
  }
}
