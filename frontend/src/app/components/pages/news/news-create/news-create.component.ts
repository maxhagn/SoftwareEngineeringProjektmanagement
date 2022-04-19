import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Form, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {NewsService} from '../../../../services/news.service';
import {Router} from '@angular/router';
import {EventService} from '../../../../services/event.service';
import {EventDto} from '../../../../dtos/event-dto';
import {News} from '../../../../dtos/news/news-dto';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../../../global/globals';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.css'],
})
export class NewsCreateComponent implements OnInit {
  /*ids*/
  public newsId;
  /*entities*/
  public news: News;
  public events: EventDto[];
  /*booleans*/
  error: boolean = false;
  reset: boolean = false;
  uploadError: boolean = false;
  errorMessage: string = '';
  public submitted: boolean = false;
  selected: boolean = false;
  /*error handling*/
  successAlert: boolean = false;
  successAlertMessage: string = '';
  /*picture*/
  pictureName: string[] = [];
  fileName: string;
  imgURL: any;
  imagePath: any;
  public selectedFile;
  newsImages: File[] = [];
  newsImage: File = null;
  images: any;

  /*add*/
  public createForm: FormGroup;
  private newsBaseUri: string = this.globals.backendUri + '/news';
  private uploaded: boolean = false;


  constructor(private formBuilder: FormBuilder,
              private httpClient: HttpClient,
              private globals: Globals,
              private newsService: NewsService,
              private eventService: EventService,
              private router: Router,
              private messageService: MessageService) {

    this.createForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      text: ['', [Validators.required]],
      author: ['', [Validators.required]],
      date: ['', [Validators.required]],
      event: ['', [Validators.required]]

    });
    this.pictureName[0] = 'Choose Picture';
    this.images = [];

  }

  ngOnInit(): void {
    this.getEvents();
    this.error = false;
    this.vanishError();
  }


  /*------------------------------------------------EVENTS------------------------------------------------------------*/
  /**
   * This method gets all events
   */
  private getEvents() {
    this.eventService.getEvents().subscribe((event: EventDto []) => {
        this.events = event;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );


  }

  /*------------------------------------------------NEWS--------------------------------------------------------------*/
  /**
   * This method sends news data to the newsService
   * @param data receive from form
   */
  public onSubmit(data: any) {
    this.error = false;
    this.submitted = true;
    if (this.createForm.valid && ('' + this.pictureName !== 'Choose Picture')) {
      const str = this.createForm.controls.text.value;
      const split = str.split(['.'], 1);
      const summary = split[0] + '...';
      const news = new News(
        null,
        this.createForm.controls.title.value,
        this.createForm.controls.text.value,
        this.createForm.controls.author.value,
        this.createForm.controls.date.value,
        summary,
        null,
        this.createForm.controls.event.value,
        null,
        null
      );


      this.createNewsEntry(news);
      this.clearForm();

    } else {
      this.error = true;
      this.createErrorMessage();
    }
  }

  /**
   * This method creates news entry
   * @param news to be created
   */
  private createNewsEntry(news: News) {
    this.submitted = true;

    this.validateInput(news);

    if (this.error === true) {
      return;
    }


    this.newsService.createNews(news)
      .subscribe(data => {
          this.newsId = data.id;
          if (this.images !== null) {
            this.uploadPictures();
            if (this.error === false && this.uploadError === false) {

              setTimeout(() => {
                this.messageService.publishMessage('Created news entry successfully');

                this.router.navigate(['/home']);
              }, 1000);


            } else if (this.uploaded === false) {
              this.errorMessage = `Invalid input: Need to set picture`;

            }


          }


        },
        error => {
          this.error = true;
          this.defaultServiceErrorHandling(error);
        });


  }

  /*------------------------------------------NEWS-IMAGE--------------------------------------------------------------*/

  /**
   * This method is triggered when an event occured, selects the file and displays the picture
   * @param event that occured
   * @param pos position of the image
   */
  whenPictureChanged(event: any, pos: number) {

    if (pos === this.pictureName.length - 1) {
      if (this.pictureName[pos] === 'Choose Picture') {
        this.pictureName.push('Choose Picture');
      }
    }


    this.uploadError = false;

    this.newsImage = event.target.files[0];
    const image = event.target.files[0];
    this.newsImages[pos] = image;
    this.selected = true;
    this.pictureName[pos] = this.newsImage.name;
    this.images = event.target.files;


    this.displaypicture(this.images);
  }


  /**
   * This method displays the picture that the user selected
   * @param files that contains information about picture
   */
  displaypicture(files) {
    const reader = new FileReader();
    this.imagePath = files;
    if (files === null) {
      this.imgURL = null;
    }

    reader.readAsDataURL(files[0]);


    reader.onload = (_event) => {
      this.imgURL = reader.result;
    };


  }

  /**
   * This method uploads a picture
   * @param newsImageElement that contains the data of the picture
   */
  uploadPicture(newsImageElement: any) {

    const uploadData = new FormData();

    uploadData.append('file', newsImageElement, newsImageElement.name);

    this.fileName = newsImageElement.name;

    const path = this.newsBaseUri + '/' + this.newsId + '/upload';

    this.httpClient.post(path, uploadData, {observe: 'response'})
      .subscribe((response) => {
          if (response.status === 200) {
            this.uploaded = true;

          }
          console.log(response.status);
        }
      );
  }


  /*------------------------------------------HELPER-METHODS----------------------------------------------------------*/

  /**
   * This method creates a error message
   */
  private createErrorMessage() {

    let errorMsg = 'Could not create news: \n';


    if (this.checkIfNull(this.createForm.controls.title.value)) {
      this.error = true;
      errorMsg += 'Need to set title\n';
    }
    if (this.checkIfNull(this.createForm.controls.author.value)) {
      this.error = true;

      errorMsg += 'Need to set author\n';
    }
    if (this.checkIfNull(this.createForm.controls.event.value)) {
      this.error = true;
      errorMsg += 'Need to set event\n';
    }
    if (this.checkIfNull(this.createForm.controls.date.value)) {
      this.error = true;
      errorMsg += 'Need to set date\n';
    }
    if (this.checkIfNull(this.createForm.controls.text.value)) {
      this.error = true;
      errorMsg += 'Need to set text\n';
    }
    if (this.newsImage === undefined || this.newsImage === null) {

      this.error = true;
      errorMsg += 'Need at least one picture\n';
    }
    if (this.checkIfNull(this.createForm.controls.event.value)
      && this.checkIfNull(this.createForm.controls.title.value)
      && this.checkIfNull(this.createForm.controls.text.value)
      && this.checkIfNull(this.createForm.controls.author.value)
      && this.checkIfNull(this.createForm.controls.date.value)) {
      this.error = true;
      this.vanishSuccessAlert();
      errorMsg = ' Values have to be set';
    }


    return errorMsg;


  }


  /**
   * Checks if entry is null, undefined  or empty
   * @param value that is checked
   */
  private checkIfNull(value: any) {
    return value === undefined
      || value === null
      || '' + value === '';

  }

  /**
   * This method validated the valid form input
   */
  private validateInput(news: News) {
    this.error = false;
    this.errorMessage = '';

    if (this.validateAttribute(news.title)) {
      this.error = true;

      this.errorMessage += 'invalid title (at least 2 words)\n';
    }
    if (this.validateAttribute(news.author)) {
      this.error = true;

      this.errorMessage += 'invalid author (at least first- & surname)\n';
    }
    if (this.validateAttribute(news.text)) {
      this.error = true;

      this.errorMessage += 'invalid text (at least 2 words)\n';
    }
    if (this.newsImage === undefined || this.newsImage === null) {

      this.error = true;
      this.errorMessage += 'Need at least one picture\n';

    }
    if (this.error === true) {
      this.error = true;
      this.images = [];
      return;
    }


  }

  /**
   * This method check if the given string only consits of spaces or if the input contains only one word
   * @param string that  is given
   */
  private validateAttribute(string: string) {

    if (!string.replace(/\s/g, '').length
      || string.split(/\s/g).length === 1) {

      return true;
    }
    return false;

  }


  /*------------------------------------------ERROR-HANDLING----------------------------------------------------------*/

  /**
   * This method handles errors
   * @param error that  occured
   */
  private defaultServiceErrorHandling(error: any) {
    this.errorMessage = 'Error handled in news-create.component';
    this.error = true;
    if (error.error instanceof ErrorEvent) {
      /*client-side or network error occurred*/
      this.errorMessage = 'An error occurred:' + error.error.message;
    }
    if (error.status === 0) { /*the backend is down*/
      this.errorMessage = 'The backend seems not to be reachable';

    } else {
      /* backend returned an unsuccessful response code*/
      this.errorMessage = `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`;

      if (error.status === 400) {
        if (this.uploadError) {
          this.errorMessage = `Invalid picture input  /*(Only JPEG and PNG are allowed)*/ `;

        } else {
          this.errorMessage = `Invalid input`;

        }


      } else if (error.status === 404) {
        this.errorMessage = `Not found`;

      }
      if (error.status === 500) {
        this.errorMessage = `Internal error`;

      }

    }
    if (this.errorMessage !== '') {
      return;
    }
  }


  /*---------------------------------------------------CLEAR----------------------------------------------------------*/
  /**
   * This method makes the picture vanish if the form was wrongly filled
   */
  private vanishImage() {

    this.pictureName = [];
    this.pictureName[0] = 'Choose Picture';
    for (let i = 0; i < this.images.length; i++) {
      this.images[i] = null;
    }

    this.newsImage = null;
    this.imgURL = null;
    this.images = [];

    this.imagePath = '';
    this.reset = true;

  }

  /**
   * This method resets the error
   */
  vanishError() {
    this.error = false;

  }


  /**
   * This method resets the success alert
   */
  vanishSuccessAlert() {
    this.successAlert = false;
  }


  /**
   * This method clears the form
   */
  private clearForm() {
    this.createForm.reset();
    if (this.error) {

      this.vanishImage();
      this.error = false;
    }

    this.submitted = false;
  }

  /**
   * This method uploads the pictures
   */
  private uploadPictures() {
    for (let i = 0; i < this.newsImages.length; i++) {
      this.uploadPicture(this.newsImages[i]);
    }
  }
}


