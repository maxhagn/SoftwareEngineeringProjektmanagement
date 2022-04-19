import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../services/auth.service';
import {NewsService} from '../../../../services/news.service';
import {NewsPreviewDto} from '../../../../dtos/news/news-preview-dto';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../../../../global/globals';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {NewsPage} from '../../../../dtos/news/news-page';



@Component({
  selector: 'app-news-view',
  templateUrl: './news-view.component.html',
  styleUrls: ['./news-view.component.css']
})
export class NewsViewComponent implements OnInit {
  private newsBaseUri: string = this.globals.backendUri + '/news';
  /*booleans*/
  displayOld: boolean = false;
  noNew: boolean = false;
  footer: boolean = true;
  lastPage: boolean = false;
  /*Entities*/
  news: NewsPreviewDto[];
  readNews: NewsPreviewDto[];
  // @ts-ignore
  page = new NewsPage();
  pageNum: number;
  size = 6;
  length = 0;

  /*errors*/
  error: boolean = false;
  uploadError: boolean = false;
  errorMessage: string = '';
  message = '';

  /*PICTURE*/
  public files: any[];
  retrieveResonse: any;
  retrievedImage: any;
  base64Data: any;
  map;

  /*Selected*/
  selectedId;

  constructor(public authService: AuthService
    , public newsService: NewsService
    , private httpClient: HttpClient
    , private globals: Globals
    , private route: ActivatedRoute) {
    this.page.content = [];
    this.pageNum = 0;
  }

  ngOnInit() {

    this.map = new Map();
    this.getUnreadNewsPage();
    this.route.paramMap.subscribe((params: ParamMap) => {
      // tslint:disable-next-line:radix
      const id = parseInt(params.get('id'));
      this.selectedId = id;

    });
    this.lastPage = false;


  }

  /*------------------------------------------------NEWS--------------------------------------------------------------*/
  /**
   * This method gets all unread news
   */
  private getUnreadNewsPage() {
    this.newsService.getUnreadNewsPage({page: this.pageNum, size: this.size}).subscribe((news: NewsPage) => {

        this.news = news.content;
        this.news.length = news.totalElements;
        if (this.news.length === 0) {

          this.noNew = true;
          this.displayOld = true;
          this.loadReadNewsPage();
        } else {
          this.noNew = false;
          this.displayOld = false;
          this.getImages(this.news);
          this.page = news;
          if (this.page.totalElements < this.size || this.pageNum < 0) {
            this.pageNum = 0;
          }
          console.log('old: ' + this.displayOld);
          this.getReadNewsPage();

          return this.page;
        }


      },
      error => {
        this.defaultServiceErrorHandling(error);
      });


  }


  /**
   * This method gets all read news
   */
  getReadNewsPage() {
    if (this.lastPage === true) {
      this.displayOld = true;
    }
    if (this.pageNum < 0) {
      this.pageNum = 0;
    }
    this.newsService.getReadNewsPage({page: this.pageNum, size: this.size}).subscribe((news: NewsPage) => {

        this.readNews = news.content;
        this.readNews.length = news.totalElements;

        if (this.readNews.length !== 0) {
          this.getImages(this.readNews);

        }


      },
      error => {
        this.defaultServiceErrorHandling(error);
      });

  }

  /**
   * This method loads only the read news
   */
  loadReadNewsPage() {
    if (this.pageNum < 0) {
      this.pageNum = 0;
    }
    this.newsService.getReadNewsPage({page: this.pageNum, size: this.size}).subscribe((news: NewsPage) => {
        this.readNews = news.content;

        this.getImages(this.readNews);
        this.page = news;

        this.readNews.length = news.totalElements;

        if (this.readNews.length !== 0) {
          this.getImages(this.readNews);

        }
        return this.page;


      },
      error => {
        this.defaultServiceErrorHandling(error);
      });

  }


  /**
   * This method displays all read news
   */
  displayOldNews() {
    if (this.pageNum !== this.page.totalPages - 1) {
      this.pageNum = this.page.totalPages - 1;
    }
    this.loadNewsPage();
    this.lastPage = true;
    this.getReadNewsPage();
  }


  /*------------------------------------------------NEWS-DISPLAY------------------------------------------------------*/
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

  /**
   * This method displays 3 entries in each row
   * @param j represents the index of news entry in the news array
   */

  /*------------------------------------------NEWS-IMAGE--------------------------------------------------------------*/

  /**
   * This gets all images
   * @param news of images to get
   */
  private getImages(news: NewsPreviewDto[]) {
    if (news !== undefined || news !== null || news.length !== 0) {


      for (let j = 0; j < news.length; j++) {
        if (news[j] === undefined) {
          continue;
        }
        const id = news[j].id;

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

  }


  /*------------------------------------------ERROR-HANDLING----------------------------------------------------------*/

  /**
   * This method handles errors
   * @param error that  occured
   */
  private defaultServiceErrorHandling(error: any) {

    this.error = true;
    if (error.error instanceof ErrorEvent) {
      /*client-side or network error occurred*/
      this.errorMessage = 'An error occurred:' + error.error.message;
    }
    if (error.status === 0) { /*the backend is down*/
      this.errorMessage = 'The backend seems not to be reachable';
    } else {
      /* backend returned an unsuccessful response code*/
      this.errorMessage =
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`;

      if (error.status === 400) {
        if (this.uploadError) {
          this.errorMessage = `Invalid picture type  (Only JPEG and PNG are allowed) `;
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

  }


  /*-------------------------NAVIGATION---------------------*/

  /*
   * This method gets the next three entries
   * @param j index of current news
   * @param news that are being displayed
   */
  getNext3Entries(j: number, news: NewsPreviewDto[]) {
    const next3 = [];

    for (let i = 0; i < 3; i++) {
      if (news[j + i] !== undefined) {
        next3[i] = news[j + i];

      }
    }

    return next3;
  }

  /**
   * This method goes to the previous page
   */
  private previousPage() {
    if (this.pageNum > 0) {
      this.pageNum--;
    }

  }


  /**
   * This method goes to the next page
   */

  private nextPage() {
    if (this.pageNum >= 0) {
      this.pageNum++;
    }
  }

  /**
   * This method loads the next page
   * if there are new news those are displayed
   * if not the readpages are displayed
   */
  loadNewsPage() {

    if (this.news !== undefined && this.news.length !== 0) {
      this.getUnreadNewsPage();
    } else {
      this.loadReadNewsPage();
    }


  }


}
