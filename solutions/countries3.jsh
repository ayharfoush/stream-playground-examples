import countries.*;
import static java.util.stream.Collectors.*;

var countries = new Countries().getAll();

// 1. Returns the largest country (care must be taken with area because it may be null):

countries.stream().max(Comparator.comparing(Country::getArea)); // throws a NullPointerException

// 2. Names of countries with null area:
countries.stream().
  filter(country -> country.getArea() == null).
  map(Country::getName).
  forEach(System.out::println);

countries.stream().
  filter(country -> country.getArea() != null).
  max(Comparator.comparing(Country::getArea));

// 3. Returns summary statistics about the area field:

countries.stream().map(Country::getArea).filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).summaryStatistics();

// 4. Returns the total area of countries:

countries.stream().
  map(Country::getArea).
  filter(Objects::nonNull).
  reduce(BigDecimal.ZERO, BigDecimal::add);

// 5. Returns a comma separated string of country names sorted alphabetically:

countries.stream().
  map(Country::getName).
  sorted().
  collect(Collector.of(() -> new StringJoiner(","), (j, s) -> j.add(s), (j1, j2) -> j1.merge(j2), j -> j.toString()))

countries.stream().
  map(Country::getName).
  sorted().
  collect(Collector.of(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge, StringJoiner::toString))

countries.stream().
  map(Country::getName).
  sorted().
  collect(Collectors.joining(","))

// 6. Returns the map of countries for efficient access by country code:

Map<String, Country> countryMap = countries.stream().
  collect(HashMap::new, (map, country) -> map.put(country.getCode(), country), HashMap::putAll);
countryMap.get("HU"); // Returns Hungary
countryMap.get("DE"); // Returns Germany
countryMap.get("CN"); // Returns China

// 7. Prints the names and populations of countries with population less or equal to that of Hungary in descending order of population:

Country hungary = countryMap.get("HU");
countries.stream().
  filter(country -> country.getPopulation() <= hungary.getPopulation()).
  sorted(Comparator.comparing(Country::getPopulation).reversed()).
  forEach(country -> System.out.printf("%s: %d\n", country.getName(), country.getPopulation()));

// 8. Returns the number of European and non-European countries:

countries.stream().collect(partitioningBy(country -> country.getRegion() == Region.EUROPE, counting()));

// 9. Returns the lists of countries by region:

Map<Region, List<Country>> countriesByRegion = countries.stream().collect(groupingBy(Country::getRegion));

// Returns the number of countries by region:

Map<Region, Long> numberOfCountriesByRegion = countries.stream().collect(groupingBy(Country::getRegion, counting()));

// 10. Prints the number of countries by region:

countries.stream().
  collect(groupingBy(Country::getRegion, counting())).
  forEach((region, count) -> System.out.printf("%s: %d\n", region, count));

countries.stream().
  collect(groupingBy(Country::getRegion, counting())).
  entrySet().
  stream().
  sorted(Comparator.comparingLong(Map.Entry::getValue)).
  forEach(System.out::println);

countries.stream().
  collect(groupingBy(Country::getRegion, counting())).
  entrySet().
  stream().
  sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).
  forEach(System.out::println);

// 11. Returns population average by region:

Map<Region, Double> avgPopulationByRegion = countries.stream().collect(groupingBy(Country::getRegion, averagingLong(Country::getPopulation)));

// 12. Returns the most populous country by region:

countries.stream().collect(groupingBy(Country::getRegion, maxBy(Comparator.comparing(Country::getPopulation))));

// 13. Returns the largest population by region:

countries.stream().collect(groupingBy(Country::getRegion, mapping(Country::getPopulation, maxBy(Long::compare))));

// 14. Returns the longest country name by region:

countries.stream().map(Country::getName).max(Comparator.comparingInt(String::length));

countries.stream().collect(groupingBy(Country::getRegion, mapping(Country::getName, maxBy(Comparator.comparingInt(String::length)))));

// 15. Returning the number of countries grouped by the first letter of their name:

countries.stream().collect(groupingBy(country -> country.getName().charAt(0), counting()));

// 16. Returns whether there are two or more countries with the same non-null area:

countries.stream().
  map(Country::getArea).
  filter(area -> area != null).
  collect(groupingBy(Function.identity(), counting())).
  entrySet().
  stream().
  anyMatch(e -> e.getValue() > 1);
