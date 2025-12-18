import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideApollo } from 'apollo-angular';
import { ApolloClientOptions, InMemoryCache } from '@apollo/client/core';
import { HttpLink } from 'apollo-angular/http';

const uri = 'http://localhost:8080/graphql'; // TODO: Use environment variable

function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  return {
    link: httpLink.create({ uri }),
    cache: new InMemoryCache(),
  };
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([]), // TODO: Add routes
    provideHttpClient(),
    provideApollo(createApollo),
  ],
}).catch(err => console.error(err));

