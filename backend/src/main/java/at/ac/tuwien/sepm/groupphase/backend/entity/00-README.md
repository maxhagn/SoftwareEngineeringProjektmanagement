* Here some information on how the enities where created:
* What do the annotations mean?
  -  @Entity: defines class as  entity
  -  @Getter: creates getter
  -  @Setter: creates setter
  -  @EqualsAndHashCode : creates equals and hashcode
  -  @ToString : creates toString
  -  @NoArgsConstructor : creates default constructor
  -  @AllArgsConstructor : creates all args constructor
  -  @Builder :
     - first use case: CLASS -> use a builder to create instances of class
     - second use case: METHOD -> use this ability to work around not being able to modify or extend ImmutableClient
                        -> method to create a builder for a final Class
  -  @Transactional : configure the transactional behavior of a method
  -  @Table(name = "EVENT_IMAGE") : sets name of table
  -  @Id : marks a field in a model class as the primary key
  -  @Column(name ="event_id", nullable= false): Define name of column & say the column is not optional (nullable=false)
* This explains the many to many relationship:  *https://www.baeldung.com/jpa-many-to-many*
* This explains what the builder does:
*https://www.baeldung.com/lombok-builder*
* This explains the one-to-many & many-to-one relationships:
*https://www.baeldung.com/hibernate-one-to-many*
*https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/*
* This explains the relationships in spring: *https://www.baeldung.com/spring-data-rest-relationships*
* This explains the Sprint Data annotations: *https://www.baeldung.com/spring-data-annotations*
* This explains Transactional: *https://www.baeldung.com/transaction-configuration-with-jpa-and-spring*
* Other links : *https://www.baeldung.com/spring-boot*