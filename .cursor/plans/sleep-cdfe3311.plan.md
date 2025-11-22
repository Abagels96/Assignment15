<!-- cdfe3311-4857-428c-9445-effe11f74ebe 26291de8-4f07-4b4d-8e69-0649381d81d6 -->
# Add Sleep Quality Selector and Wire to POST

## Changes

- Add a select input for sleep quality in `src/main/resources/templates/track.html` (values must match `SleepQuality` enum names).
- Update `src/main/resources/static/track.js` to read the selected quality and include it in the request when `activityType === 'SLEEP'`.
- Optional (cleaner API): add a dedicated POST endpoint that accepts `Sleep` for detailed sessions, else keep existing `/record` and let backend ignore non-sleep fields.
- Backend already has `@Enumerated(EnumType.STRING)` on `Sleep.quality`; ensure validation rejects invalid values.

## Key snippets

- HTML select (placed near Sleep button):
```html
<label for="sleep-quality" class="block text-sm text-gray-600">Sleep Quality</label>
<select id="sleep-quality" class="mt-1 p-2 border rounded w-full">
  <option value="POOR">Poor</option>
  <option value="AVERAGE">Average</option>
  <option value="GOOD" selected>Good</option>
  <option value="EXCELLENT">Excellent</option>
</select>
```

- JS: read quality only for sleep and include in body:
```javascript
async function recordActivity(activityType) {
  const isSleep = activityType === 'SLEEP';
  const quality = isSleep ? document.getElementById('sleep-quality')?.value : undefined;

  const newActivity = {
    type: activityType,
    timestamp: new Date().toISOString(),
    ...(isSleep && { startDateTime: new Date().toISOString() }),
    ...(isSleep && { endDateTime: new Date().toISOString() }),
    ...(isSleep && { quality })
  };
  // POST as before
}
```

- Optional: if moving to a dedicated endpoint for sleep:
```javascript
const url = isSleep ? `${API_BASE_URL}/record/sleep` : `${API_BASE_URL}/record`;
```


## Todos

- Add select UI to track.html
- Wire quality read in track.js for SLEEP
- Optional: add /record/sleep endpoint & validation

### To-dos

- [ ] Add sleep quality <select> to track.html near Sleep button
- [ ] Read selected quality in track.js and include when SLEEP
- [ ] Add POST /selfcare/record/sleep accepting Sleep and validate