import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/sub/header/header.component';
import {FooterComponent} from './components/sub/footer/footer.component';
import {HomeComponent} from './components/pages/home/home.component';
import {LoginComponent} from './components/pages/auth/sign-in/login.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { EventsEntryComponent } from './components/sub/events-entry/events-entry.component';
import { PerformancesEntryComponent } from './components/sub/performances-entry/performances-entry.component';
import { HallViewComponent } from './components/sub/hall-view/hall-view.component';
import { SignUpComponent } from './components/pages/auth/sign-up/sign-up.component';
import { EventsChartComponent } from './components/sub/events-chart/events-chart.component';
import { ArtistsEntryComponent } from './components/sub/artists-entry/artists-entry.component';
import { LocationsEntryComponent } from './components/sub/locations-entry/locations-entry.component';
import { PerformancesCreateEntryComponent } from './components/sub/performances-create-entry/performances-create-entry.component';
import { TicketEntryComponent } from './components/sub/ticket-entry/ticket-entry.component';
import { NewsDetailComponent } from './components/pages/news/news-detail/news-detail.component';
import { NewsCreateComponent } from './components/pages/news/news-create/news-create.component';
import { ProfileComponent } from './components/pages/profile/profile.component';
import { EventsTopComponent } from './components/pages/events/events-top/events-top.component';
import { EventsDetailComponent } from './components/pages/events/events-detail/events-detail.component';
import { ArtistsDetailComponent } from './components/pages/artists/artists-detail/artists-detail.component';
import { LocationsDetailComponent } from './components/pages/locations/locations-detail/locations-detail.component';
import { EventsCreateComponent } from './components/pages/events/events-create/events-create.component';
import { HallPlanComponent } from './components/pages/hall/hall-plan/hall-plan.component';
import { TicketsSeatSelectComponent } from './components/pages/tickets/tickets-seat-select/tickets-seat-select.component';
import { CheckoutBuyComponent } from './components/pages/checkout/checkout-buy/checkout-buy.component';
import { CheckoutReserveComponent } from './components/pages/checkout/checkout-reserve/checkout-reserve.component';
import { TicketsDetailComponent } from './components/pages/tickets/tickets-detail/tickets-detail.component';
import { ReversalInvoivePrintComponent } from './components/pages/reversal-invoive-print/reversal-invoive-print.component';
import { UsersCreateComponent } from './components/pages/users/users-create/users-create.component';
import { UsersDetailComponent } from './components/pages/users/users-detail/users-detail.component';
import { TicketsPrintComponent } from './components/pages/tickets/tickets-print/tickets-print.component';
import { LocationsCreateComponent } from './components/pages/locations/locations-create/locations-create.component';
import { HallCreateComponent } from './components/pages/hall/hall-create/hall-create.component';
import { ArtistsViewComponent } from './components/pages/artists/artists-view/artists-view.component';
import { BillsPrintComponent } from './components/pages/bills/bills-print/bills-print.component';
import { EventsViewComponent } from './components/pages/events/events-view/events-view.component';
import { LocationsViewComponent } from './components/pages/locations/locations-view/locations-view.component';
import { NewsViewComponent } from './components/pages/news/news-view/news-view.component';
import { PerformancesViewComponent } from './components/pages/performances/performances-view/performances-view.component';
import { TicketsViewComponent } from './components/pages/tickets/tickets-view/tickets-view.component';
import { UsersViewComponent } from './components/pages/users/users-view/users-view.component';
import { UsersEntryComponent } from './components/sub/users-entry/users-entry.component';
import { ContentHeaderComponent } from './components/sub/helper/content-header/content-header.component';
import { InputBoxComponent } from './components/sub/helper/input-box/input-box.component';
import { FormBoxComponent } from './components/sub/helper/form-box/form-box.component';
import { SearchBoxComponent } from './components/sub/helper/search-box/search-box.component';
import { PerformancesPageComponent } from './components/pages/performances/performances-page/performances-page.component';
import { PerformancesPageEntryComponent } from './components/sub/performances-page-entry/performances-page-entry.component';
import { ErrorBoxComponent } from './components/sub/helper/error-box/error-box.component';
import { InfoBoxComponent} from './components/sub/helper/info-box/info-box.component';
import { LoadingIndicatorComponent } from './components/sub/helper/loading-indicator/loading-indicator.component';
import { CheckoutDetailViewComponent } from './components/sub/checkout-detail-view/checkout-detail-view.component';
import { TicketsFinalizeComponent } from './components/pages/tickets/tickets-finalize/tickets-finalize.component';
import { NgxBarcode6Module } from 'ngx-barcode6';
import { ResetPwComponent } from './components/pages/auth/reset-pw/reset-pw.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    EventsEntryComponent,
    PerformancesEntryComponent,
    HallViewComponent,
    SignUpComponent,
    EventsChartComponent,
    ArtistsEntryComponent,
    LocationsEntryComponent,
    PerformancesCreateEntryComponent,
    TicketEntryComponent,
    NewsDetailComponent,
    NewsCreateComponent,
    ProfileComponent,
    EventsTopComponent,
    EventsDetailComponent,
    ArtistsDetailComponent,
    LocationsDetailComponent,
    EventsCreateComponent,
    HallPlanComponent,
    TicketsSeatSelectComponent,
    CheckoutBuyComponent,
    CheckoutReserveComponent,
    TicketsDetailComponent,
    ReversalInvoivePrintComponent,
    UsersCreateComponent,
    UsersDetailComponent,
    TicketsPrintComponent,
    LocationsCreateComponent,
    HallCreateComponent,
    ArtistsViewComponent,
    BillsPrintComponent,
    EventsViewComponent,
    LocationsViewComponent,
    NewsViewComponent,
    PerformancesViewComponent,
    TicketsViewComponent,
    UsersViewComponent,
    UsersEntryComponent,
    ContentHeaderComponent,
    InputBoxComponent,
    FormBoxComponent,
    SearchBoxComponent,
    PerformancesPageComponent,
    PerformancesPageEntryComponent,
    ErrorBoxComponent,
    InfoBoxComponent,
    LoadingIndicatorComponent,
    CheckoutDetailViewComponent,
    TicketsFinalizeComponent,
    ResetPwComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    NgxBarcode6Module
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
