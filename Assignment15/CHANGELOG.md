# Changelog - Shower Activity Feature Implementation

## Overview
This document details all changes made to implement the shower activity tracking feature with rating and length recording capabilities.

**Date:** Current Session  
**Feature:** Shower Activity Form with Rating and Length Tracking

---

## Summary of Changes

### Request
Create a shower form that allows users to:
- Provide a rating (1-5 scale)
- Record length in minutes
- View the rating and length when submitting
- Display rating and length in the activity history

---

## Files Modified

### 1. `src/main/resources/templates/track.html`

**Changes Made:**
- Added a new shower form section with ID `shower-form-section`
- Form includes:
  - Rating dropdown selector (ONE through FIVE, mapped to 1-5)
  - Length input field (number type, minimum 1 minute)
  - Submit and Cancel buttons
- Styled with sky-blue theme to match the shower button color scheme
- Form is hidden by default and shown when Shower button is clicked

**Location:** Inserted after the Sleep Form Section (around line 127)

**Code Added:**
```html
<!-- Shower Form Section -->
<section id="shower-form-section" class="mb-6 hidden">
    <div class="bg-sky-50 border border-sky-200 rounded-lg p-4 space-y-4">
        <h3 class="text-lg font-semibold text-sky-800 mb-3">Shower Details</h3>
        <!-- Rating dropdown -->
        <!-- Length input -->
        <!-- Action buttons -->
    </div>
</section>
```

---

### 2. `src/main/resources/static/track.js`

**Changes Made:**

#### a. Updated `recordActivity()` function
- Added logic to detect `SHOWER` type activity
- When SHOWER button is clicked, shows the shower form instead of immediately recording
- Location: Lines ~116-152

**Code Change:**
```javascript
// Added check for shower
if (isShower) {
    showShowerForm();
    return;
}
```

#### b. Added `showShowerForm()` function
- Removes `hidden` class from shower form section
- Sets default rating to "THREE" (3/5)
- Clears previous length input
- Scrolls to form for better UX
- Location: Lines ~185-202

#### c. Added `hideShowerForm()` function
- Adds `hidden` class to hide the shower form
- Location: Lines ~204-209

#### d. Added `submitShower()` function
- Validates rating and length inputs
- Ensures length is at least 1 minute
- Creates shower activity object with:
  - Type: 'SHOWER'
  - Timestamp: Current ISO timestamp
  - Rating: Selected rating enum value
  - LengthInMinutes: Parsed integer value
- Sends POST request to `/selfcare/record/shower`
- Shows success alert with formatted rating (1-5) and length
- Hides form and reloads history after successful submission
- Location: Lines ~241-285

**Key Features:**
- Input validation (rating required, length must be >= 1)
- User-friendly alert message showing rating as "X/5" format
- Error handling with user feedback

#### e. Updated `renderHistory()` function
- Added handling for SHOWER activity type
- Extracts `rating` and `lengthInMinutes` from shower activities
- Converts rating enum (ONE, TWO, etc.) to numeric display (1, 2, etc.)
- Displays shower activities with format: "Rating: X/5 | Length: Y min"
- Location: Lines ~56-119

**Display Format for Shower:**
```
shower | [timestamp] | Rating: 3/5 | Length: 15 min
```

**Code Change:**
```javascript
else if (activity.type === 'SHOWER') {
    timeToDisplay = activity.timestamp;
    rating = activity.rating;
    lengthInMinutes = activity.lengthInMinutes;
    // ... display formatting with rating conversion
}
```

---

### 3. `src/main/java/com/coderscampus/Assignment15/web/SelfCareController.java`

**Changes Made:**
- Added new endpoint: `POST /selfcare/record/shower`
- Endpoint accepts `Shower` object in request body
- Validates:
  - Rating must not be null
  - LengthInMinutes must not be null and must be >= 1
- Sets timestamp if not provided
- Returns saved `Shower` object with HTTP 201 (CREATED)
- Returns HTTP 400 (BAD_REQUEST) if validation fails
- Location: Lines ~89-108

**New Endpoint Details:**
```java
@PostMapping("/record/shower")
public ResponseEntity<Shower> recordShower(@RequestBody Shower shower)
```

**Validation Logic:**
- Checks `shower.getRating() == null` → 400 Bad Request
- Checks `shower.getLengthInMinutes() == null || < 1` → 400 Bad Request
- Sets `shower.setTimestamp(Instant.now())` if null

---

## API Endpoints

### New Endpoint

#### `POST /selfcare/record/shower`
- **Purpose:** Record a shower activity with rating and length
- **Request Body:**
  ```json
  {
    "type": "SHOWER",
    "timestamp": "2024-01-15T10:30:00Z",
    "rating": "THREE",
    "lengthInMinutes": 15
  }
  ```
- **Response (Success - 201 Created):**
  ```json
  {
    "id": 1,
    "timestamp": "2024-01-15T10:30:00Z",
    "type": "SHOWER",
    "rating": "THREE",
    "lengthInMinutes": 15
  }
  ```
- **Response (Error - 400 Bad Request):**
  - Missing or invalid rating
  - Missing or invalid length (< 1 minute)

---

## User Flow

1. **User clicks Shower button** → `recordActivity('SHOWER')` called
2. **Shower form appears** → `showShowerForm()` displays form
3. **User fills form:**
   - Selects rating (1-5, default: 3)
   - Enters length in minutes
4. **User clicks "Record Shower"** → `submitShower()` called
5. **Validation occurs:**
   - Rating must be selected
   - Length must be >= 1 minute
6. **Data sent to backend** → `POST /selfcare/record/shower`
7. **Success alert shown** → "Shower recorded! Rating: 3/5 Length: 15 minutes"
8. **History reloaded** → New shower entry appears with rating and length displayed

---

## Display Format

### History List Display
Shower activities are displayed in the history list with the following format:
```
[shower] [timestamp] Rating: 3/5 | Length: 15 min
```

### Success Alert
When a shower is successfully recorded, an alert displays:
```
Shower recorded!
Rating: 3/5
Length: 15 minutes
```

---

## Data Model

### Shower Entity (Already Existed)
- **Class:** `com.coderscampus.Assignment15.domain.Shower`
- **Extends:** `Activity`
- **Fields:**
  - `rating` (Rating enum: ONE, TWO, THREE, FOUR, FIVE)
  - `lengthInMinutes` (Integer)

### Rating Enum (Already Existed)
- **Class:** `com.coderscampus.Assignment15.domain.Rating`
- **Values:** ONE, TWO, THREE, FOUR, FIVE

---

## Technical Details

### Frontend
- **Framework:** Vanilla JavaScript
- **Styling:** Tailwind CSS
- **API Base URL:** `http://localhost:8080/selfcare`

### Backend
- **Framework:** Spring Boot
- **Controller:** `SelfCareController`
- **Service:** `SelfCareService`
- **Repository:** `ActivityRepository`

### Validation
- **Frontend:** JavaScript validation before API call
- **Backend:** Java validation in controller endpoint
- **Database:** JPA entity constraints

---

## Testing Checklist

- [x] Shower form appears when Shower button is clicked
- [x] Form can be cancelled (hidden without submission)
- [x] Rating dropdown works (1-5 options)
- [x] Length input accepts numeric values
- [x] Validation prevents submission with invalid data
- [x] Success alert shows correct rating and length
- [x] History displays shower activities with rating and length
- [x] Rating is displayed as numeric (1-5) not enum (ONE-FIVE)
- [x] Backend endpoint validates and saves shower data
- [x] Error handling for network failures

---

## Notes

1. **Rating Display:** The rating enum values (ONE, TWO, etc.) are converted to numeric format (1, 2, etc.) for user-friendly display in both the alert and history list.

2. **Form Styling:** The shower form uses sky-blue color scheme (`bg-sky-50`, `border-sky-200`, etc.) to match the shower button's color.

3. **Default Values:** The form defaults to rating "THREE" (3/5) when shown.

4. **Consistency:** The implementation follows the same pattern as the existing sleep form for consistency in code structure and user experience.

5. **No Database Changes:** The `Shower` entity and `Rating` enum already existed in the codebase, so no database schema changes were required.

---

## Future Enhancements (Not Implemented)

Potential improvements that could be added:
- Edit existing shower records
- Filter history by activity type
- Statistics/analytics for shower activities
- Average shower length calculation
- Rating trends over time

---

## Files Summary

| File | Type | Status | Lines Changed |
|------|------|--------|---------------|
| `track.html` | Template | Modified | ~30 lines added |
| `track.js` | JavaScript | Modified | ~100 lines added/modified |
| `SelfCareController.java` | Java | Modified | ~20 lines added |

---

**End of Changelog**

