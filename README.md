# Tech Test Application

## Purpose
The purpose of this application is to store reading data and calculate additional metricks

## Endpoints



*GET* `/api/smart/reads/{ACCOUNTNUMBER}`
- returns the given user account along with all historic readings

*expected payload*
```
{
    "accountId": Number,
    "gasReadings": [
        {
            "id": Number,
            "meterId": Number,
            "reading": Number,
            "usageSinceLastRead": Number,
            "periodSinceLastRead": Number,
            "avgDailyUsage": Number,
            "date": Date
        }
    ],
    "elecReadings": [
        {
            "id": Number,
            "meterId": Number,
            "reading": Number,
            "usageSinceLastRead": Number,
            "periodSinceLastRead": Number,
            "avgDailyUsage": Number,
            "date": Date
        }
    ]
}
```

*Main Body Field Breakdown*
- `accountId` primary identified for account
- `gasReadings` data structure for gas readings
- `elecReadings` data structure for electrical readings

*Reading Field Breakdown*
- `id` identification for the given reading data
- `meterId` assigned id for the given meter
- `reading` reading taken
- `usageSinceLastRead` the difference between the given reading and the previous reading
- `periodSinceLastRead` number of days between the current and previous meter reading
- `avgDailyUsage` the average usage between the current and previous reading
- `date` date the reading was taken

*POST* `/api/smart/reads`

- Allows the registration of meter information

*Request Payload*
```
{
    "accountId": Number,
    "gasReadings": [
        {
            "id": Number,
            "meterId": Number,
            "reading": Number,
            "date": Date
        },
        {
            "id": Number,
            "meterId": Number,
            "reading": Number,
            "date": Date
        }
    ]
}
```

*Main Body Field Breakdown*
- `accountId` primary identified for account
- `gasReadings` data structure for gas readings
- `elecReadings` data structure for electrical readings

*Reading Field Breakdown*
- `id` identification for the given reading data
- `meterId` assigned id for the given meter
- `reading` reading taken
- `date` date the reading was taken

## Class Breakdown
Detailed below is the class structure of the service. Details on throught process and areas for improvement.

### ReadingApplication.java
This class is intended to perform top level buisiness logic required by the application. Functionality of this class includes:
- Retrieval of entries from the database
- Persistence of entries to the database
- Verification of duplicate entries
- Top level enrichment for additional fields
- Population of initial datapoints

Points of note within this class, When a user intends to create a new entry into the readings database, a requirement is to ensure
that there are no duplicated readings (Defined as readings from the same meter on the same day)
Part of this process involves ensure that not only are duplicates not present between user data and pre-existing date, but also
to ensure that there are no duplicates within the inbound user data as well. In order to accomplish this, before the data is saved
it is placed into a composite list of readings where then it is verified that no two entries match the given criteria for a duplicate.
After this is successful, the list is then sent on to the enrichmentHander class to enrich the required analytics fields.

A further point of note is the @PostConstruct method within the application class. Typically, an SQL database may be seeded via
the use of Flyway. However, due to time constraints this could not be implemented in a timely fashion, and so a decision was taken
to have the data populated on startup via springboot. Future work would see this data refactored into a Flyway or other data
migration tool.

### EnrichmentHandler.java
This class is to perform the required logic in order to populate the additional fields of 
- `usageSinceLastRead` the difference between the given reading and the previous reading
- `periodSinceLastRead` number of days between the current and previous meter reading
- `avgDailyUsage` the average usage between the current and previous reading

The enrichment process involves first identifying the most recent reading relative to the reading to be enriched.
The data required is then extracted from the previous reading and is used to calculate each of the above data fields.

Using a separate class helps to encapsulate the enrichment code, which would otherwise heavily bloat the ReadingsApplication class.
An identified weakness to implementation is the duplication of code, as the two readings classes are very similar.
An approach for refactor would be to have the two readings classes inherit from a BaseReading class or interface and allow
the required data to be accessed via a parent. Unfortunately, this could not be implemented within the timeboxed period and
so the decision was taken to implement the duplicated code.