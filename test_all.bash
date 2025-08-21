#!/usr/bin/env bash
#
# Sample usage:
#   ./test_all.bash start stop
#   start and stop are optional
#
#   HOST=localhost PORT=8080 ./test_all.bash
#

: ${HOST=localhost}
: ${PORT=8080}

# arrays to hold all our test data ids
allTestLeagueIds=()
allTestVenueIds=()
allTestTeamIds=()
allTestMatchIds=()

# check HTTP status
function assertCurl() {
  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result
  result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]; then
    if [ "$httpCode" = "200" ]; then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, Response: $RESPONSE)"
    fi
  else
    echo "Test FAILED, expected HTTP $expectedHttpCode but got $httpCode"
    echo "- Command: $curlCmd"
    echo "- Response: $RESPONSE"
    exit 1
  fi
}

# compare values
function assertEqual() {
  local expected=$1
  local actual=$2
  if [ "$actual" = "$expected" ]; then
    echo "Test OK (value: $actual)"
  else
    echo "Test FAILED, expected value $expected but got $actual"
    exit 1
  fi
}

# check service ready
function testUrl() {
  local url=$@
  if curl -ks -f -o /dev/null $url; then
    return 0
  else
    return 1
  fi
}

function waitForService() {
  local url=$@
  echo -n "Waiting for $url ..."
  until testUrl $url; do
    echo -n "."
    sleep 2
  done
  echo " ready"
}

# prepare test data
function setupTestdata() {
  # create a league
  body='{
    "name": "NWSL",
    "country": "USA",
    "format": "LEAGUE",
    "numberOfTeams": 20,
    "leagueDifficulty": "High",
    "seasonYear": 2026,
    "seasonStartDate": "2026-07-01",
    "seasonEndDate": "2027-01-15",
    "competitionFormatType": "LEAGUE",
    "competitionFormatGroupStage": false,
    "competitionFormatKnockout": false
  }'
  leagueId=$(curl -s -X POST http://$HOST:$PORT/api/v1/leagues \
    -H "Content-Type: application/json" \
    -d "$body" | jq -r '.leagueId')
  allTestLeagueIds[1]=$leagueId
  echo "Created leagueId: $leagueId"

  # create a venue
  body='{
    "name": "Wembley Stadium",
    "capacity": 90000,
    "city": "London",
    "yearBuilt": 2007,
    "venueState": "PAST"
  }'
  venueId=$(curl -s -X POST http://$HOST:$PORT/api/v1/venues \
    -H "Content-Type: application/json" \
    -d "$body" | jq -r '.venueId')
  allTestVenueIds[1]=$venueId
  echo "Created venueId: $venueId"

  # create a team
  body='{
    "teamName": "Chelsea FC",
    "coach": "Mauricio Pochettino",
    "foundingYear": 1905,
    "budget": 450000000.00,
    "teamStatus": "IS_PLAYING"
  }'
  teamId=$(curl -s -X POST http://$HOST:$PORT/api/v1/teams \
    -H "Content-Type: application/json" \
    -d "$body" | jq -r '.teamId')
  allTestTeamIds[1]=$teamId
  echo "Created teamId: $teamId"
}

# start of script
set -e

echo "HOST=$HOST"
echo "PORT=$PORT"

if [[ $@ == *"start"* ]]; then
  docker-compose down
  docker-compose up -d
fi

# wait for each service endpoint
waitForService http://$HOST:$PORT/api/v1/leagues
waitForService http://$HOST:$PORT/api/v1/venues
waitForService http://$HOST:$PORT/api/v1/teams

# prepare data
setupTestdata

# --------------------
# LEAGUE tests
# --------------------

echo
echo "Test 1: GET all leagues"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/leagues"

echo
echo "Test 2: GET league by valid ID"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/leagues/${allTestLeagueIds[1]}"

echo
echo "Test 3: GET league by non-existent ID → 404"
assertCurl 404 "curl -s http://$HOST:$PORT/api/v1/leagues/00000000-0000-0000-0000-000000000000"

echo
echo "Test 4: GET league by invalid ID → 422"
assertCurl 422 "curl -s http://$HOST:$PORT/api/v1/leagues/invalid-id"

echo
echo "Test 5: POST new league"
body='{
  "name": "Test League",
  "country": "USA",
  "format": "LEAGUE",
  "numberOfTeams": 20,
  "leagueDifficulty": "Low",
  "seasonYear": 2025,
  "seasonStartDate": "2025-05-01",
  "seasonEndDate": "2025-11-01",
  "competitionFormatType": "LEAGUE",
  "competitionFormatGroupStage": false,
  "competitionFormatKnockout": false
}'
assertCurl 201 "curl -s -X POST http://$HOST:$PORT/api/v1/leagues -H \"Content-Type: application/json\" -d '$body'"
newLeagueName=$(echo "$RESPONSE" | jq -r '.name')
assertEqual "Test League" "$newLeagueName"

echo
echo "Test 6: PUT update existing league"
updateBody='{
  "name": "Test League Updated",
  "country": "USA",
  "format": "LEAGUE",
  "numberOfTeams": 20,
  "leagueDifficulty": "Medium",
  "seasonYear": 2025,
  "seasonStartDate": "2025-06-01",
  "seasonEndDate": "2025-12-01",
  "competitionFormatType": "LEAGUE",
  "competitionFormatGroupStage": false,
  "competitionFormatKnockout": false
}'
assertCurl 200 "curl -s -X PUT http://$HOST:$PORT/api/v1/leagues/${allTestLeagueIds[1]} -H \"Content-Type: application/json\" -d '$updateBody'"
updatedName=$(echo "$RESPONSE" | jq -r '.name')
assertEqual "Test League Updated" "$updatedName"

echo
echo "Test 7: DELETE existing league"
assertCurl 204 "curl -s -X DELETE http://$HOST:$PORT/api/v1/leagues/${allTestLeagueIds[1]}"

# --------------------
# VENUE tests
# --------------------

echo
echo "Test V1: GET all venues"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/venues"

echo
echo "Test V2: GET venue by valid ID"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/venues/${allTestVenueIds[1]}"

echo
echo "Test V3: GET venue non-existent → 404"
assertCurl 404 "curl -s http://$HOST:$PORT/api/v1/venues/00000000-0000-0000-0000-000000000000"

echo
echo "Test V4: GET venue invalid ID → 422"
assertCurl 422 "curl -s http://$HOST:$PORT/api/v1/venues/bad-id"

echo
echo "Test V5: POST new venue"
body='{
  "name": "New Stadium",
  "capacity": 50000,
  "city": "Paris",
  "yearBuilt": 2018,
  "venueState": "UPCOMING"
}'
assertCurl 201 "curl -s -X POST http://$HOST:$PORT/api/v1/venues -H \"Content-Type: application/json\" -d '$body'"
newVenueCity=$(echo "$RESPONSE" | jq -r '.city')
assertEqual "Paris" "$newVenueCity"

echo
echo "Test V6: PUT update venue"
updateBody='{
  "name": "New Stadium Renovated",
  "capacity": 55000,
  "city": "Paris",
  "yearBuilt": 2018,
  "venueState": "UPCOMING"
}'
assertCurl 200 "curl -s -X PUT http://$HOST:$PORT/api/v1/venues/${allTestVenueIds[1]} -H \"Content-Type: application/json\" -d '$updateBody'"
updatedCapacity=$(echo "$RESPONSE" | jq -r '.capacity')
assertEqual "55000" "$updatedCapacity"

echo
echo "Test V7: DELETE existing venue"
assertCurl 204 "curl -s -X DELETE http://$HOST:$PORT/api/v1/venues/${allTestVenueIds[1]}"

# --------------------
# TEAM tests
# --------------------

echo
echo "Test T1: GET all teams"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/teams"

echo
echo "Test T2: GET team by valid ID"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/teams/${allTestTeamIds[1]}"

echo
echo "Test T3: GET team non-existent → 404"
assertCurl 404 "curl -s http://$HOST:$PORT/api/v1/teams/00000000-0000-0000-0000-000000000000"

echo
echo "Test T4: GET team invalid ID → 422"
assertCurl 422 "curl -s http://$HOST:$PORT/api/v1/teams/xyz"

echo
echo "Test T5: POST new team"
body='{
  "teamName": "Test FC",
  "coach": "Test Coach",
  "foundingYear": 2000,
  "budget": 1000000.00,
  "teamStatus": "IS_PLAYING"
}'
assertCurl 201 "curl -s -X POST http://$HOST:$PORT/api/v1/teams -H \"Content-Type: application/json\" -d '$body'"
newTeamName=$(echo "$RESPONSE" | jq -r '.teamName')


echo
echo "Test T6: PUT update team"
updateBody='{
  "teamName": "Test FC Updated",
  "coach": "Test Coach Updated",
  "foundingYear": 2000,
  "budget": 2000000.00,
  "teamStatus": "IS_PLAYING"
}'
assertCurl 200 "curl -s -X PUT http://$HOST:$PORT/api/v1/teams/${allTestTeamIds[1]} -H \"Content-Type: application/json\" -d '$updateBody'"
updatedCoach=$(echo "$RESPONSE" | jq -r '.coach')
assertEqual "Test Coach Updated" "$updatedCoach"

echo
echo "Test T7: DELETE existing team"
assertCurl 204 "curl -s -X DELETE http://$HOST:$PORT/api/v1/teams/${allTestTeamIds[1]}"

# --------------------
# MATCH tests
# --------------------

# we need a league, venue, team for matches
setupTestdata

echo
echo "Test M1: GET all matches in league"
leagueId=${allTestLeagueIds[1]}
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/leagues/$leagueId/matches"

echo
echo "Test M2: POST new match"
matchBody='{
  "matchScore": "2-1",
  "matchStatus": "SCHEDULED",
  "matchTime": "15:00:00",
  "matchDate": "2025-05-10",
  "matchDuration": "01:30:00",
  "resultsType": "DRAW",
  "matchMinute": 45,
  "venueId": "'"${allTestVenueIds[1]}"'",
  "teamId": "'"${allTestTeamIds[1]}"'"
}'
assertCurl 201 "curl -s -X POST http://$HOST:$PORT/api/v1/leagues/$leagueId/matches -H \"Content-Type: application/json\" -d '$matchBody'"
allTestMatchIds[1]=$(echo "$RESPONSE" | jq -r '.matchId')
echo "Created matchId: ${allTestMatchIds[1]}"

echo
echo "Test M3: GET match by valid ID"
assertCurl 200 "curl -s http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/${allTestMatchIds[1]}"

echo
echo "Test M4: GET match non-existent → 404"
assertCurl 404 "curl -s http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/00000000-0000-0000-0000-000000000000"

echo
echo "Test M5: GET match invalid ID → 422"
assertCurl 422 "curl -s http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/invalid-id"

echo
echo "Test M6: PUT update match"
updateMatch='{
  "matchScore": "3-2",
  "matchStatus": "COMPLETED",
  "matchTime": "18:00:00",
  "matchDate": "2025-05-11",
  "matchDuration": "01:45:00",
  "resultsType": "WINNER",
  "matchMinute": 90,
  "venueId": "'"${allTestVenueIds[1]}"'",
  "teamId": "'"${allTestTeamIds[1]}"'"
}'
assertCurl 200 "curl -s -X PUT http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/${allTestMatchIds[1]} -H \"Content-Type: application/json\" -d '$updateMatch'"
updatedStatus=$(echo "$RESPONSE" | jq -r '.matchStatus')
assertEqual "COMPLETED" "$updatedStatus"

echo
echo "Test M7: DELETE existing match"
assertCurl 204 "curl -s -X DELETE http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/${allTestMatchIds[1]}"

echo
echo "Test M8: PUT match non-existent → 404"
assertCurl 404 "curl -s -X PUT http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/00000000-0000-0000-0000-000000000000 -X PUT -H \"Content-Type: application/json\" -d '$updateMatch'"

echo
echo "Test M9: PUT match invalid ID → 422"
assertCurl 422 "curl -s -X PUT http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/invalid-id -H \"Content-Type: application/json\" -d '$updateMatch'"

echo
echo "Test M10: DELETE match non-existent → 404"
assertCurl 404 "curl -s -X DELETE http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/00000000-0000-0000-0000-000000000000"

echo
echo "Test M11: DELETE match invalid ID → 422"
assertCurl 422 "curl -s -X DELETE http://$HOST:$PORT/api/v1/leagues/$leagueId/matches/invalid-id"
# cleanup
if [[ $@ == *"stop"* ]]; then
  docker-compose down
fi
