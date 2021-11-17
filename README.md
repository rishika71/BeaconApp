# Shopping with Beacon App (In Proximity)

We used Bluecats Beacons provided to us by Prof. Shehab for AMAD Course Project 9. https://www.bluecats.com/

## Requirements
This assignment focuses on using bluetooth beacons to provide indoor proximity information in a grocery store environment. The app you are going to work on assumes a grocery store which has several aisles on which different products are placed. Bluetooth Beacons will places on each of the store’s aisles. The app should provide the user a context aware customized list of discounted products for the aisle closest to the user. The below figure shows the layout of the store and the location of the different beacons.

When the user is in the store, the app should locate the closest beacon and present only the products for the region belonging to the closest beacon.

As the user moves the app should contact the api to get the list of discounted items in the closest region, and the list should be refreshed to show the retrieved list of discounted products for the closest region.

Your application should avoid oscillating between regions, which is when the app during a scan assumes region 1 then in the next scan assumes region 2, and then region 1. This case might happen when the user is equidistant from multiple beacons or due to errors in the distance estimations. This will affect the user experience and your app should present a usable solution to this problem.

If the app is unable to locate any beacons it should display all the discounted products sorted by region.

![beacon](https://imgur.com/FrV8gSu.png)

## Authors
- Sneh Jain
- Rishika Mathur

## API Design & Implementation

- The API used can be found at `https://mysterious-beach-05426.herokuapp.com/`. When the user's location is
detected to be in a specific beacon region, the API `https://mysterious-beach-05426.herokuapp.com/product/getAll` is used to get products in that aisle with discounts.

```
[{"_id":"5fd1385cbaa0aa8d484ce8ff","discount":10,"name":"Pineapple","photo":"https://firebasestorage.googleapis.com/v0/b/chatroom-7b49e.appspot.com/o/Beacons%20Store%20Item%20Images%2Fpineapple.png?alt=media&token=aaa973b4-3b2b-410b-85b6-f7597282f596","price":1.18,"region":"produce"},
{"_id":"5fd138afbaa0aa8d484d01fa","discount":10,"name":"Oranges","photo":"https://firebasestorage.googleapis.com/v0/b/chatroom-7b49e.appspot.com/o/Beacons%20Store%20Item%20Images%2Foranges.png?alt=media&token=084e8481-e6a4-416a-b502-b9e1954555f4","price":0.89,"region":"produce"},
...]
```
