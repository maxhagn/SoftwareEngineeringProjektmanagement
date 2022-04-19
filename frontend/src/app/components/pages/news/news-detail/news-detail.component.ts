import {Component, OnInit, ViewChild} from '@angular/core';
import {NewsService} from '../../../../services/news.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {News} from '../../../../dtos/news/news-dto';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../../../global/globals';
import {NewsPreviewDto} from '../../../../dtos/news/news-preview-dto';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../../../services/auth.service';
import {HelperUtils} from '../../../../global/helper-utils.service';



@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.css']
})
export class NewsDetailComponent implements OnInit {


  /*Ids*/
  newsId;
  news: News;
  // @ts-ignore
  newsEntries: NewsPreviewDto[];

  /*Error message*/
  errorMessage = '';
  error = false;

  /*PICTURE*/
  public files: any[];
  retrieveResonse: any;
  retrievedImage: any;
  retrievedImages: any[];
  base64Data: any;
  map;

  read = false;
  threeEntries: NewsPreviewDto[];


  private newsBaseUri: string = this.globals.backendUri + '/news';


  constructor(private newService: NewsService,
              private route: ActivatedRoute,
              private router: Router,
              private httpClient: HttpClient,
              private globals: Globals,
              public authService: AuthService,
              private helperUtils: HelperUtils) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      // tslint:disable-next-line:radix
      const id = parseInt(params.get('id'));
      this.newsId = id;

    });
    this.map = new Map();
    this.loadNews(this.newsId);


  }

  /**
   * This method loads the news entry with the given id
   * @param id of news entry
   */
  private loadNews(id: number) {
    this.newService.getNewsById(id).subscribe((news: News) => {
      this.news = news;
      this.postRead();


    }, error => {
      this.defaultServiceErrorHandling(error);
    });
    this.getPictures(id);


  }

  /**
   * This method lists the unread news entries
   */
  private listUnreadNews() {
    this.newService.listUnreadNews().subscribe((news: NewsPreviewDto []) => {

        this.newsEntries = news;
        this.getImages();
        this.get3Entries(this.newsId);


      },
      error => {
        this.defaultServiceErrorHandling(error);
      });

  }

  /**
   * This method gets the pictures of the news entry
   * @param id of news entry
   */
  private getPictures(id: number) {

    const path = this.newsBaseUri + '/' + id + '/pics';
    this.httpClient.get(path).subscribe(res => {

        if (res !== null) {


          this.retrieveResonse = res;

          const retrievedImg = [];
          for (let i = 0; i < this.retrieveResonse.length; i++) {
            const retievesRes = this.retrieveResonse[i];
            const base64Data = retievesRes.pic;
            retrievedImg[i] = 'data:image/jpeg;base64,' + base64Data;
          }

          this.retrievedImages = retrievedImg;
        }


      },
      error => {
        this.defaultServiceErrorHandling(error);
      });


  }

  /**
   * This method returns the month in letters
   * @param date where the month is retrieved
   */
  private getMonth(date: string) {
    const strings = date.split('-');

    const month = strings[1];

    switch (month) {
      case '01':
        return 'Jan';
      case '02':
        return 'Feb';
      case '03':
        return 'Mar';
      case '04':
        return 'Apr';
      case '05':
        return 'May';
      case '06':
        return 'Jun';
      case '07':
        return 'Jul';
      case '08':
        return 'Aug';
      case '09':
        return 'Sep';
      case '10':
        return 'Oct';
      case '11':
        return 'Nov';
      case '12':
        return 'Dec';
      default:
        return 'No month';
    }


  }

  /**
   * This method returns the month in letters
   * @param date where the month is retrieved
   */
  getDay(date: string) {
    return date.split('-')[2];
  }

  getYear(date: string) {
    return date.split('-')[0];
  }

  get3Entries(j: number) {
    let next3 = [];
    if (this.newsEntries !== undefined) {
      next3 = this.loadNewsEntries();
    }
    this.threeEntries = next3;
  }




  /*------------------ERROR-HANDLING------------------*/

  /**
   * This method handles  errors
   * @param error that occured
   */
  private defaultServiceErrorHandling(error: any) {
    this.error = true;
    if (error.error instanceof ErrorEvent) {
      /*client-side or network error occurred*/
      console.error('An error occurred:', error.error.message);
    }
    if (error.status === 0) { /*the backend is down*/
      this.errorMessage = 'The backend seems not to be reachable';
    } else {
      /* backend returned an unsuccessful response code*/
      this.errorMessage = `Backend returned code ${error.status}, ` + `body was: ${error.error}`;
    }


  }

  /**
   * This  method gets the image
   */
  private getImages() {

    for (let j = 0; j < this.newsEntries.length; j++) {
      const id = this.newsEntries[j].id;

      const path = this.newsBaseUri + '/' + id + '/pic';
      this.httpClient.get(path).subscribe(res => {

          if (res !== null) {
            this.retrieveResonse = res;
            this.base64Data = this.retrieveResonse.pic;
            this.retrievedImage = 'data:image/jpeg;base64,' + this.base64Data;
            this.map.set(id, this.retrievedImage);

          }


        },
        error => {
          this.defaultServiceErrorHandling(error);
        });

    }

  }

  /**
   * This method loads the news entry if  selected
   * @param id of news
   */
  whenSelected(id: any) {
    this.router.navigate(['/news/' + id]);
    this.loadNews(id);
    this.newsId = id;
    this.postRead();
  }

  /**
   * This method sends a post request
   */
  private postRead() {
    this.newService.postReaderNew(this.newsId).subscribe(
      () => {
        this.read = true;
        this.listUnreadNews();
      },
      (error: any) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    );

  }



  /**
   * This method loads new news entries
   */
  private loadNewsEntries() {
    console.log('Len:' + this.newsEntries.length);
    const entries = [];
    for (let j = 0; j < 3; j++) {
      console.log('J: ' + j);
      if (this.newsEntries[j] !== undefined) {
        entries[j] = this.newsEntries[j];
      }


    }
    return entries;


  }
}
