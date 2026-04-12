const API_BASE = "/api";
const TOKEN_KEY = "fitness_access_token";
const REFRESH_KEY = "fitness_refresh_token";
const NOTICE_TIMEOUT_MS = 3500;

const state = {
  exercisesPage: 0,
  exercisesTotalPages: 1,
  exerciseFilters: {},
  categories: [],
  exercises: [],
  workouts: [],
  days: [],
  details: {
    categories: null,
    exercises: null,
    workouts: null,
  },
  editing: {
    categoryId: null,
    exerciseId: null,
    workoutId: null,
    dayDate: null,
    dayId: null,
  },
  activeModalForm: null,
  noticeTimer: null,
};

const elements = {
  authScreen: document.getElementById("authScreen"),
  appScreen: document.getElementById("appScreen"),
  authNotice: document.getElementById("authNotice"),
  notice: document.getElementById("notice"),
  entityModal: document.getElementById("entityModal"),
  entityModalTitle: document.getElementById("entityModalTitle"),
  closeEntityModalBtn: document.getElementById("closeEntityModalBtn"),
  showLoginBtn: document.getElementById("showLoginBtn"),
  showRegisterBtn: document.getElementById("showRegisterBtn"),
  loginForm: document.getElementById("loginForm"),
  registerForm: document.getElementById("registerForm"),
  logoutBtn: document.getElementById("logoutBtn"),
  sectionTabs: Array.from(document.querySelectorAll(".section-tab")),
  sections: Array.from(document.querySelectorAll(".section")),
  openCategoryModalBtn: document.getElementById("openCategoryModalBtn"),
  openExerciseModalBtn: document.getElementById("openExerciseModalBtn"),
  openWorkoutModalBtn: document.getElementById("openWorkoutModalBtn"),
  openDayModalBtn: document.getElementById("openDayModalBtn"),
  categoryForm: document.getElementById("categoryForm"),
  categorySubmitBtn: document.getElementById("categorySubmitBtn"),
  categoriesList: document.getElementById("categoriesList"),
  exerciseForm: document.getElementById("exerciseForm"),
  exerciseSubmitBtn: document.getElementById("exerciseSubmitBtn"),
  exerciseCategorySelect: document.getElementById("exerciseCategorySelect"),
  exerciseFilterForm: document.getElementById("exerciseFilterForm"),
  exerciseFilterCategorySelect: document.getElementById("exerciseFilterCategorySelect"),
  clearExerciseFilterBtn: document.getElementById("clearExerciseFilterBtn"),
  exercisesList: document.getElementById("exercisesList"),
  prevExercisesBtn: document.getElementById("prevExercisesBtn"),
  nextExercisesBtn: document.getElementById("nextExercisesBtn"),
  exercisesPageInfo: document.getElementById("exercisesPageInfo"),
  workoutForm: document.getElementById("workoutForm"),
  workoutSubmitBtn: document.getElementById("workoutSubmitBtn"),
  workoutExerciseChoices: document.getElementById("workoutExerciseChoices"),
  workoutsList: document.getElementById("workoutsList"),
  dayForm: document.getElementById("dayForm"),
  daySubmitBtn: document.getElementById("daySubmitBtn"),
  dayCalendarInput: document.getElementById("dayCalendarInput"),
  dayWorkoutSelect: document.getElementById("dayWorkoutSelect"),
  daysList: document.getElementById("daysList"),
  mediaForm: document.getElementById("mediaForm"),
  mediaFileInput: document.getElementById("mediaFileInput"),
  pickMediaFileBtn: document.getElementById("pickMediaFileBtn"),
  mediaFileName: document.getElementById("mediaFileName"),
  mediaResult: document.getElementById("mediaResult"),
};

function getAccessToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function setTokens(tokens) {
  localStorage.setItem(TOKEN_KEY, tokens.access);
  localStorage.setItem(REFRESH_KEY, tokens.refresh);
}

function clearTokens() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_KEY);
}

function getDayWorkoutId(day) {
  return day.wokroutId ?? day.workoutId ?? null;
}

function formatWeekday(dateValue) {
  return new Intl.DateTimeFormat("ru-RU", { weekday: "long" }).format(new Date(dateValue));
}

function formatWeekdayShort(dateValue) {
  return new Intl.DateTimeFormat("ru-RU", { weekday: "short" }).format(new Date(dateValue));
}

function formatDateRu(dateValue) {
  const [year, month, day] = String(dateValue).split("-");
  if (!year || !month || !day) {
    return "";
  }
  return `${day}/${month}/${year}`;
}

function setDayDisplayFromIso(isoDate) {
  elements.dayForm.elements.dateDisplay.value = formatDateRu(isoDate);
  elements.dayCalendarInput.value = isoDate;
}

function openDayCalendarFromDateInput() {
  if (typeof elements.dayCalendarInput.showPicker === "function") {
    elements.dayCalendarInput.showPicker();
  } else {
    elements.dayCalendarInput.click();
  }
}

function pickNoticeTarget(scope = "auto") {
  if (scope === "auth") {
    return elements.authNotice;
  }
  if (scope === "app") {
    return elements.notice;
  }
  return elements.appScreen.classList.contains("hidden") ? elements.authNotice : elements.notice;
}

function clearNotice(scope = "all") {
  if (state.noticeTimer) {
    clearTimeout(state.noticeTimer);
    state.noticeTimer = null;
  }

  const targets = scope === "all" ? [elements.notice, elements.authNotice] : [pickNoticeTarget(scope)];
  targets.forEach((target) => {
    target.textContent = "";
    target.classList.add("hidden");
    target.classList.remove("success", "error");
  });
}

function showNotice(message, type = "info", scope = "auto", autoHide = true) {
  const target = pickNoticeTarget(scope);
  target.textContent = message;
  target.classList.remove("hidden", "success", "error");
  if (type === "success") {
    target.classList.add("success");
  } else if (type === "error") {
    target.classList.add("error");
  }

  if (scope !== "auth" && autoHide) {
    if (state.noticeTimer) {
      clearTimeout(state.noticeTimer);
    }
    state.noticeTimer = setTimeout(() => clearNotice("app"), NOTICE_TIMEOUT_MS);
  }
}

function setAuthView(mode) {
  const showLogin = mode === "login";
  elements.showLoginBtn.classList.toggle("active", showLogin);
  elements.showRegisterBtn.classList.toggle("active", !showLogin);
  elements.loginForm.classList.toggle("active", showLogin);
  elements.registerForm.classList.toggle("active", !showLogin);
}

function setMainSection(sectionName) {
  elements.sectionTabs.forEach((tab) => {
    tab.classList.toggle("active", tab.dataset.section === sectionName);
  });
  elements.sections.forEach((section) => {
    section.classList.toggle("active", section.id === `section-${sectionName}`);
  });
}

function hideAllModalForms() {
  [elements.categoryForm, elements.exerciseForm, elements.workoutForm, elements.dayForm].forEach((form) => {
    form.classList.add("hidden");
  });
}

function closeEntityModal() {
  elements.entityModal.classList.add("hidden");
  hideAllModalForms();
  state.activeModalForm = null;
}

function openEntityModal(formName, title) {
  hideAllModalForms();
  elements.entityModalTitle.textContent = title;
  const form = elements[`${formName}Form`];
  form.classList.remove("hidden");
  elements.entityModal.classList.remove("hidden");
  state.activeModalForm = formName;
}

function applyAuthState(isAuthenticated) {
  elements.authScreen.classList.toggle("hidden", isAuthenticated);
  elements.appScreen.classList.toggle("hidden", !isAuthenticated);
  clearNotice("all");
}

async function tryRefreshToken() {
  const refresh = localStorage.getItem(REFRESH_KEY);
  if (!refresh) {
    return false;
  }
  try {
    const response = await fetch(`${API_BASE}/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refresh }),
    });
    if (!response.ok) {
      return false;
    }
    setTokens(await response.json());
    return true;
  } catch {
    return false;
  }
}

function mapHttpError(status) {
  if (status === 502 || status === 503 || status === 504) {
    return "Сервер временно недоступен. Попробуйте обновить страницу через несколько секунд.";
  }
  return `Ошибка ${status}`;
}

async function api(path, options = {}, retried = false) {
  const headers = { ...(options.headers || {}) };
  const token = getAccessToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  } catch {
    throw new Error("Нет соединения с сервером. Проверьте, что backend запущен.");
  }

  if (response.status === 401 && !retried) {
    const refreshed = await tryRefreshToken();
    if (refreshed) {
      return api(path, options, true);
    }
    clearTokens();
    applyAuthState(false);
    throw new Error("Сессия истекла. Выполните вход снова.");
  }

  if (!response.ok) {
    let message = mapHttpError(response.status);
    try {
      const contentType = response.headers.get("content-type") || "";
      if (contentType.includes("application/json")) {
        const body = await response.json();
        message = body.message || message;
      }
    } catch {
      // ignore parse errors
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }
  return response.json();
}

function resetPrivateDataUI() {
  state.categories = [];
  state.exercises = [];
  state.workouts = [];
  state.days = [];
  state.exercisesPage = 0;
  state.exercisesTotalPages = 1;
  state.exerciseFilters = {};
  state.details = { categories: null, exercises: null, workouts: null };
  state.editing = { categoryId: null, exerciseId: null, workoutId: null, dayDate: null, dayId: null };
  state.activeModalForm = null;

  elements.categoriesList.innerHTML = "";
  elements.exercisesList.innerHTML = "";
  elements.workoutsList.innerHTML = "";
  elements.daysList.innerHTML = "";
  elements.workoutExerciseChoices.innerHTML = "";
  elements.exerciseCategorySelect.innerHTML = "";
  elements.exerciseFilterCategorySelect.innerHTML = "<option value=\"\">Все категории</option>";
  elements.dayWorkoutSelect.innerHTML = "";
  elements.mediaFileName.textContent = "Файл не выбран";
  elements.mediaResult.textContent = "Файл еще не загружен.";
  elements.exercisesPageInfo.textContent = "Страница 1 / 1";
  closeEntityModal();
}

function getCategoryById(id) {
  return state.categories.find((item) => item.id === id) || null;
}

function getWorkoutById(id) {
  return state.workouts.find((item) => item.id === id) || null;
}

function renderSelectOptions() {
  const categoryOptions = state.categories
    .map((category) => `<option value="${category.id}">${category.name}</option>`)
    .join("");
  elements.exerciseCategorySelect.innerHTML = categoryOptions || "<option value=\"\">Нет категорий</option>";

  const filterOptions = state.categories
    .map((category) => `<option value="${category.id}">${category.name}</option>`)
    .join("");
  elements.exerciseFilterCategorySelect.innerHTML = `<option value="">Все категории</option>${filterOptions}`;

  const workoutOptions = state.workouts
    .map((workout) => `<option value="${workout.id}">${workout.name}</option>`)
    .join("");
  elements.dayWorkoutSelect.innerHTML = workoutOptions || "<option value=\"\">Нет тренировок</option>";
}

function renderWorkoutExerciseChoices() {
  elements.workoutExerciseChoices.innerHTML = "";
  if (!state.exercises.length) {
    elements.workoutExerciseChoices.innerHTML = "<p class=\"muted\">Сначала добавьте упражнения.</p>";
    return;
  }

  state.exercises.forEach((exercise) => {
    const chip = document.createElement("label");
    chip.className = "choice-item";
    chip.innerHTML = `
      <input type="checkbox" name="exerciseIds" value="${exercise.id}">
      <span>${exercise.name}</span>
    `;
    elements.workoutExerciseChoices.appendChild(chip);
  });

  syncWorkoutEditSelection();
}

function syncWorkoutEditSelection() {
  if (!state.editing.workoutId) {
    return;
  }
  const workout = getWorkoutById(state.editing.workoutId);
  if (!workout) {
    resetEditingState();
    return;
  }
  const selected = new Set(workout.exerciseIds || []);
  const checkboxes = elements.workoutForm.querySelectorAll('input[name="exerciseIds"]');
  checkboxes.forEach((checkbox) => {
    checkbox.checked = selected.has(Number(checkbox.value));
  });
}

function resetEditingState() {
  state.editing = { categoryId: null, exerciseId: null, workoutId: null, dayDate: null, dayId: null };
}

function startCategoryEdit(category) {
  resetEditingState();
  state.editing.categoryId = category.id;
  elements.categoryForm.reset();
  elements.categoryForm.elements.name.value = category.name || "";
  elements.categorySubmitBtn.textContent = "Сохранить";
  openEntityModal("category", "Изменение категории");
}

function startExerciseEdit(exercise) {
  resetEditingState();
  state.editing.exerciseId = exercise.id;
  elements.exerciseForm.reset();
  elements.exerciseForm.elements.name.value = exercise.name || "";
  elements.exerciseForm.elements.durationMinutes.value = exercise.durationMinutes || "";
  elements.exerciseForm.elements.categoryId.value = String(exercise.categoryId || "");
  elements.exerciseSubmitBtn.textContent = "Сохранить";
  openEntityModal("exercise", "Изменение упражнения");
}

function startWorkoutEdit(workout) {
  resetEditingState();
  state.editing.workoutId = workout.id;
  elements.workoutForm.reset();
  elements.workoutForm.elements.name.value = workout.name || "";
  elements.workoutSubmitBtn.textContent = "Сохранить";
  openEntityModal("workout", "Изменение тренировки");
  syncWorkoutEditSelection();
}

function startDayEdit(day) {
  resetEditingState();
  state.editing.dayDate = day.date;
  state.editing.dayId = day.id;
  elements.dayForm.reset();
  setDayDisplayFromIso(day.date);
  elements.dayForm.elements.dateDisplay.readOnly = true;
  elements.dayForm.elements.workoutId.value = String(getDayWorkoutId(day) || "");
  elements.dayForm.elements.calories.value = day.calories || "";
  elements.daySubmitBtn.textContent = "Сохранить";
  openEntityModal("day", "Изменение дня");
}

function prepareCreateCategory() {
  resetEditingState();
  elements.categoryForm.reset();
  elements.categorySubmitBtn.textContent = "Создать";
  openEntityModal("category", "Новая категория");
}

function prepareCreateExercise() {
  resetEditingState();
  elements.exerciseForm.reset();
  elements.exerciseSubmitBtn.textContent = "Создать";
  openEntityModal("exercise", "Новое упражнение");
}

function prepareCreateWorkout() {
  resetEditingState();
  elements.workoutForm.reset();
  elements.workoutSubmitBtn.textContent = "Создать";
  openEntityModal("workout", "Новая тренировка");
  renderWorkoutExerciseChoices();
}

function prepareCreateDay() {
  resetEditingState();
  elements.dayForm.reset();
  elements.dayForm.elements.dateDisplay.readOnly = true;
  elements.dayCalendarInput.value = "";
  elements.daySubmitBtn.textContent = "Создать";
  openEntityModal("day", "Новый тренировочный день");
}

function createDetailContainer(html, isLoading = false) {
  const container = document.createElement("div");
  container.className = "inline-detail";
  if (isLoading) {
    container.innerHTML = "<p class=\"muted\">Загрузка...</p>";
  } else {
    container.innerHTML = html;
  }
  return container;
}

function allKnownWeekdays() {
  const unique = Array.from(
    new Set(
      state.days
        .map((day) => formatWeekdayShort(day.date))
        .filter(Boolean)
    )
  );
  return unique.sort((a, b) => a.localeCompare(b, "ru"));
}

function weekdayListForWorkout(workoutId) {
  const weekdays = Array.from(
    new Set(
      state.days
        .filter((day) => getDayWorkoutId(day) === workoutId)
        .map((day) => formatWeekdayShort(day.date))
    )
  );
  if (weekdays.length) {
    return weekdays.sort((a, b) => a.localeCompare(b, "ru"));
  }
  return allKnownWeekdays();
}

function formatWorkoutDaysLabel(workoutId) {
  const days = weekdayListForWorkout(workoutId);
  return days.length ? days.join(", ") : "не указаны";
}

function renderCategories() {
  elements.categoriesList.innerHTML = "";
  if (!state.categories.length) {
    elements.categoriesList.innerHTML = "<p class=\"muted\">Категории пока не созданы.</p>";
    return;
  }

  state.categories.forEach((category) => {
    const card = document.createElement("article");
    card.className = "card";
    card.dataset.entity = "categories";
    card.dataset.id = String(category.id);

    const header = document.createElement("div");
    header.className = "card-head";
    header.innerHTML = `<h4>${category.name}</h4>`;

    const actions = document.createElement("div");
    actions.className = "card-actions";

    const detailBtn = document.createElement("button");
    detailBtn.type = "button";
    detailBtn.className = "secondary";
    detailBtn.textContent = state.details.categories === category.id ? "Скрыть" : "Подробнее";
    detailBtn.addEventListener("click", async () => {
      if (state.details.categories === category.id) {
        state.details.categories = null;
        renderCategories();
        return;
      }
      state.details.categories = category.id;
      renderCategories();
      await loadCategoryDetail(category.id);
    });

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "danger";
    deleteBtn.textContent = "Удалить";
    deleteBtn.addEventListener("click", async () => {
      try {
        await api(`/categories/${category.id}`, { method: "DELETE" });
        await refreshAllData();
        showNotice("Категория удалена.", "success");
      } catch (error) {
        showNotice(error.message, "error");
      }
    });

    const editBtn = document.createElement("button");
    editBtn.type = "button";
    editBtn.className = "secondary";
    editBtn.textContent = "Редактировать";
    editBtn.addEventListener("click", () => startCategoryEdit(category));

    actions.append(detailBtn, editBtn, deleteBtn);
    header.appendChild(actions);
    card.appendChild(header);

    if (state.details.categories === category.id) {
      card.appendChild(createDetailContainer("", true));
    }

    elements.categoriesList.appendChild(card);
  });
}

function renderExercises() {
  elements.exercisesList.innerHTML = "";
  if (!state.exercises.length) {
    elements.exercisesList.innerHTML = "<p class=\"muted\">Упражнения не найдены.</p>";
    return;
  }

  state.exercises.forEach((exercise) => {
    const category = getCategoryById(exercise.categoryId);
    const card = document.createElement("article");
    card.className = "card";
    card.dataset.entity = "exercises";
    card.dataset.id = String(exercise.id);
    card.innerHTML = `
      <div class="card-head">
        <h4>${exercise.name}</h4>
        <p class="muted">${exercise.durationMinutes} мин</p>
      </div>
      <p class="meta">Категория: ${category ? category.name : "Без категории"}</p>
    `;

    const actions = document.createElement("div");
    actions.className = "card-actions";

    const detailBtn = document.createElement("button");
    detailBtn.type = "button";
    detailBtn.className = "secondary";
    detailBtn.textContent = state.details.exercises === exercise.id ? "Скрыть" : "Подробнее";
    detailBtn.addEventListener("click", async () => {
      if (state.details.exercises === exercise.id) {
        state.details.exercises = null;
        renderExercises();
        return;
      }
      state.details.exercises = exercise.id;
      renderExercises();
      await loadExerciseDetail(exercise.id);
    });

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "danger";
    deleteBtn.textContent = "Удалить";
    deleteBtn.addEventListener("click", async () => {
      try {
        await api(`/exercises/${exercise.id}`, { method: "DELETE" });
        await refreshAllData();
        showNotice("Упражнение удалено.", "success");
      } catch (error) {
        showNotice(error.message, "error");
      }
    });

    const editBtn = document.createElement("button");
    editBtn.type = "button";
    editBtn.className = "secondary";
    editBtn.textContent = "Редактировать";
    editBtn.addEventListener("click", () => startExerciseEdit(exercise));

    actions.append(detailBtn, editBtn, deleteBtn);
    card.appendChild(actions);

    if (state.details.exercises === exercise.id) {
      card.appendChild(createDetailContainer("", true));
    }

    elements.exercisesList.appendChild(card);
  });

  elements.exercisesPageInfo.textContent = `Страница ${state.exercisesPage + 1} / ${state.exercisesTotalPages}`;
  elements.prevExercisesBtn.disabled = state.exercisesPage === 0;
  elements.nextExercisesBtn.disabled = state.exercisesPage + 1 >= state.exercisesTotalPages;
}

function renderWorkouts() {
  elements.workoutsList.innerHTML = "";
  if (!state.workouts.length) {
    elements.workoutsList.innerHTML = "<p class=\"muted\">Тренировки пока не созданы.</p>";
    return;
  }

  state.workouts.forEach((workout) => {
    const relatedDays = state.days.filter((day) => getDayWorkoutId(day) === workout.id);
    const card = document.createElement("article");
    card.className = "card";
    card.dataset.entity = "workouts";
    card.dataset.id = String(workout.id);
    card.innerHTML = `
      <div class="card-head">
        <h4>${workout.name}</h4>
        <p class="muted">Проведено: ${relatedDays.length} ${declension(relatedDays.length, ["раз", "раза", "раз"])}</p>
      </div>
      <p class="meta">Дни: ${formatWorkoutDaysLabel(workout.id)}</p>
    `;

    const actions = document.createElement("div");
    actions.className = "card-actions";

    const detailBtn = document.createElement("button");
    detailBtn.type = "button";
    detailBtn.className = "secondary";
    detailBtn.textContent = state.details.workouts === workout.id ? "Скрыть" : "Подробнее";
    detailBtn.addEventListener("click", async () => {
      if (state.details.workouts === workout.id) {
        state.details.workouts = null;
        renderWorkouts();
        return;
      }
      state.details.workouts = workout.id;
      renderWorkouts();
      await loadWorkoutDetail(workout.id);
    });

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "danger";
    deleteBtn.textContent = "Удалить";
    deleteBtn.addEventListener("click", async () => {
      try {
        await api(`/workouts/${workout.id}`, { method: "DELETE" });
        await refreshAllData();
        showNotice("Тренировка удалена.", "success");
      } catch (error) {
        showNotice(error.message, "error");
      }
    });

    const editBtn = document.createElement("button");
    editBtn.type = "button";
    editBtn.className = "secondary";
    editBtn.textContent = "Редактировать";
    editBtn.addEventListener("click", () => startWorkoutEdit(workout));

    actions.append(detailBtn, editBtn, deleteBtn);
    card.appendChild(actions);

    if (state.details.workouts === workout.id) {
      card.appendChild(createDetailContainer("", true));
    }

    elements.workoutsList.appendChild(card);
  });
}

function renderDays() {
  elements.daysList.innerHTML = "";
  if (!state.days.length) {
    elements.daysList.innerHTML = "<p class=\"muted\">История тренировок пока пуста.</p>";
    return;
  }

  const sortedDays = [...state.days].sort((a, b) => b.date.localeCompare(a.date));
  sortedDays.forEach((day) => {
    const workout = getWorkoutById(getDayWorkoutId(day));
    const card = document.createElement("article");
    card.className = "card";
    card.innerHTML = `
      <div class="card-head">
        <h4>${workout ? workout.name : "Тренировка"}</h4>
        <p class="muted">${day.calories} ккал</p>
      </div>
      <p class="meta">${formatDateRu(day.date)}, ${formatWeekday(day.date)}</p>
    `;

    const actions = document.createElement("div");
    actions.className = "card-actions";
    const editBtn = document.createElement("button");
    editBtn.type = "button";
    editBtn.className = "secondary";
    editBtn.textContent = "Редактировать";
    editBtn.addEventListener("click", () => startDayEdit(day));

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "danger";
    deleteBtn.textContent = "Удалить запись";
    deleteBtn.addEventListener("click", async () => {
      try {
        await api(`/days/${day.id}`, { method: "DELETE" });
        await refreshAllData();
        showNotice("Запись дня удалена.", "success");
      } catch (error) {
        showNotice(error.message, "error");
      }
    });
    actions.append(editBtn, deleteBtn);
    card.appendChild(actions);
    elements.daysList.appendChild(card);
  });
}

function updateCardDetail(entity, id, html) {
  const selector = `.card[data-entity="${entity}"][data-id="${id}"]`;
  const card = document.querySelector(selector);
  if (!card) {
    return;
  }

  const existing = card.querySelector(".inline-detail");
  if (existing) {
    existing.innerHTML = html;
  } else {
    card.appendChild(createDetailContainer(html));
  }
}

async function loadCategoryDetail(categoryId) {
  try {
    const response = await api(`/exercises?categoryId=${categoryId}&page=0&size=100`);
    const exercises = Array.isArray(response) ? response : response.content || [];
    const exerciseNames = exercises.length
      ? exercises.map((item) => `<li>${item.name} (${item.durationMinutes} мин)</li>`).join("")
      : "<li>Упражнений пока нет.</li>";

    updateCardDetail("categories", categoryId, `
      <p class="muted">Упражнения категории:</p>
      <ul class="detail-list">${exerciseNames}</ul>
    `);
  } catch (error) {
    updateCardDetail("categories", categoryId, `<p class="error-inline">${error.message}</p>`);
  }
}

async function loadExerciseDetail(exerciseId) {
  try {
    const exercise = await api(`/exercises/${exerciseId}`);
    const category = getCategoryById(exercise.categoryId);
    const workouts = state.workouts.filter((workout) => (workout.exerciseIds || []).includes(exercise.id));
    const workoutNames = workouts.length
      ? workouts.map((item) => `<li>${item.name}</li>`).join("")
      : "<li>Не используется в тренировках.</li>";

    updateCardDetail("exercises", exerciseId, `
      <p><strong>Категория:</strong> ${category ? category.name : "Без категории"}</p>
      <p><strong>Длительность:</strong> ${exercise.durationMinutes} мин</p>
      <p class="muted">Используется в тренировках:</p>
      <ul class="detail-list">${workoutNames}</ul>
    `);
  } catch (error) {
    updateCardDetail("exercises", exerciseId, `<p class="error-inline">${error.message}</p>`);
  }
}

async function loadWorkoutDetail(workoutId) {
  try {
    const workout = await api(`/workouts/${workoutId}`);
    const exercises = await Promise.all(
      (workout.exerciseIds || []).map(async (exerciseId) => {
        try {
          return await api(`/exercises/${exerciseId}`);
        } catch {
          return null;
        }
      })
    );
    const safeExercises = exercises.filter(Boolean);
    const exerciseItems = safeExercises.length
      ? safeExercises.map((item) => `<li>${item.name} (${item.durationMinutes} мин)</li>`).join("")
      : "<li>Упражнения не добавлены.</li>";

    const days = state.days
      .filter((day) => getDayWorkoutId(day) === workout.id)
      .sort((a, b) => b.date.localeCompare(a.date));
    const dayItems = days.length
      ? days.map((day) => `<li>${formatDateRu(day.date)}, ${formatWeekday(day.date)} - ${day.calories} ккал</li>`).join("")
      : "<li>Проведений еще нет.</li>";

    const fallbackDays = days.length ? "" : `<p class="muted">Дни недели: ${formatWorkoutDaysLabel(workout.id)}</p>`;

    updateCardDetail("workouts", workoutId, `
      <p class="muted">Состав тренировки:</p>
      <ul class="detail-list">${exerciseItems}</ul>
      <p class="muted">Даты и дни проведения:</p>
      <ul class="detail-list">${dayItems}</ul>
      ${fallbackDays}
    `);
  } catch (error) {
    updateCardDetail("workouts", workoutId, `<p class="error-inline">${error.message}</p>`);
  }
}

function declension(value, forms) {
  const n = Math.abs(value) % 100;
  const n1 = n % 10;
  if (n > 10 && n < 20) {
    return forms[2];
  }
  if (n1 > 1 && n1 < 5) {
    return forms[1];
  }
  if (n1 === 1) {
    return forms[0];
  }
  return forms[2];
}

async function loadCategories() {
  state.categories = await api("/categories");
}

async function loadExercises() {
  const params = new URLSearchParams({
    page: String(state.exercisesPage),
    size: "6",
  });
  Object.entries(state.exerciseFilters).forEach(([key, value]) => {
    if (value) {
      params.set(key, String(value));
    }
  });

  const result = await api(`/exercises?${params.toString()}`);
  state.exercises = Array.isArray(result) ? result : result.content || [];
  state.exercisesTotalPages = Array.isArray(result) ? 1 : Math.max(1, result.totalPages || 1);
}

async function loadWorkouts() {
  state.workouts = await api("/workouts");
}

async function loadDays() {
  state.days = await api("/days");
}

function parseIdsFromForm(form, fieldName) {
  return Array.from(form.querySelectorAll(`input[name="${fieldName}"]:checked`))
    .map((item) => Number(item.value))
    .filter((value) => Number.isFinite(value) && value > 0);
}

async function refreshAllData() {
  await Promise.all([loadCategories(), loadExercises(), loadWorkouts(), loadDays()]);
  if (state.editing.workoutId && !getWorkoutById(state.editing.workoutId)) {
    resetEditingState();
    closeEntityModal();
  }
  renderSelectOptions();
  renderWorkoutExerciseChoices();
  renderCategories();
  renderExercises();
  renderWorkouts();
  renderDays();
}

elements.showLoginBtn.addEventListener("click", () => setAuthView("login"));
elements.showRegisterBtn.addEventListener("click", () => setAuthView("register"));

elements.sectionTabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    setMainSection(tab.dataset.section);
  });
});

elements.openCategoryModalBtn.addEventListener("click", prepareCreateCategory);
elements.openExerciseModalBtn.addEventListener("click", prepareCreateExercise);
elements.openWorkoutModalBtn.addEventListener("click", prepareCreateWorkout);
elements.openDayModalBtn.addEventListener("click", prepareCreateDay);
elements.dayForm.elements.dateDisplay.addEventListener("click", openDayCalendarFromDateInput);
elements.dayForm.elements.dateDisplay.addEventListener("focus", openDayCalendarFromDateInput);
elements.dayCalendarInput.addEventListener("change", () => {
  if (elements.dayCalendarInput.value) {
    setDayDisplayFromIso(elements.dayCalendarInput.value);
  }
});
elements.pickMediaFileBtn.addEventListener("click", () => elements.mediaFileInput.click());
elements.mediaFileInput.addEventListener("change", () => {
  const file = elements.mediaFileInput.files && elements.mediaFileInput.files[0];
  elements.mediaFileName.textContent = file ? file.name : "Файл не выбран";
});

elements.closeEntityModalBtn.addEventListener("click", closeEntityModal);
elements.entityModal.addEventListener("click", (event) => {
  if (event.target === elements.entityModal) {
    closeEntityModal();
  }
});
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && !elements.entityModal.classList.contains("hidden")) {
    closeEntityModal();
  }
});

elements.loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  try {
    const tokens = await api("/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: data.get("username"),
        password: data.get("password"),
      }),
    });
    setTokens(tokens);
    applyAuthState(true);
    await refreshAllData();
    showNotice("Вход выполнен.", "success");
    form.reset();
  } catch (error) {
    showNotice(error.message, "error", "auth", false);
  }
});

elements.registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  try {
    const tokens = await api("/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: data.get("username"),
        email: data.get("email"),
        password: data.get("password"),
      }),
    });
    setTokens(tokens);
    applyAuthState(true);
    await refreshAllData();
    showNotice("Регистрация завершена.", "success");
    form.reset();
  } catch (error) {
    showNotice(error.message, "error", "auth", false);
  }
});

elements.logoutBtn.addEventListener("click", () => {
  clearTokens();
  resetPrivateDataUI();
  applyAuthState(false);
  setAuthView("login");
});

elements.categoryForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  try {
    if (state.editing.categoryId) {
      await api(`/categories/${state.editing.categoryId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: data.get("name") }),
      });
    } else {
      await api("/categories", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: data.get("name") }),
      });
    }
    await refreshAllData();
    closeEntityModal();
    showNotice(state.editing.categoryId ? "Категория обновлена." : "Категория создана.", "success");
    resetEditingState();
    form.reset();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.exerciseForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  try {
    const payload = {
      name: data.get("name"),
      durationMinutes: Number(data.get("durationMinutes")),
      categoryId: Number(data.get("categoryId")),
    };
    if (state.editing.exerciseId) {
      await api(`/exercises/${state.editing.exerciseId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    } else {
      await api("/exercises", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    }
    await refreshAllData();
    closeEntityModal();
    showNotice(state.editing.exerciseId ? "Упражнение обновлено." : "Упражнение создано.", "success");
    resetEditingState();
    form.reset();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.exerciseFilterForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  state.exerciseFilters = {
    name: data.get("name"),
    categoryId: data.get("categoryId"),
    durationMinutes: data.get("durationMinutes"),
  };
  state.exercisesPage = 0;
  try {
    await loadExercises();
    renderExercises();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.clearExerciseFilterBtn.addEventListener("click", async () => {
  elements.exerciseFilterForm.reset();
  state.exerciseFilters = {};
  state.exercisesPage = 0;
  try {
    await loadExercises();
    renderExercises();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.prevExercisesBtn.addEventListener("click", async () => {
  if (state.exercisesPage === 0) {
    return;
  }
  state.exercisesPage -= 1;
  try {
    await loadExercises();
    renderExercises();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.nextExercisesBtn.addEventListener("click", async () => {
  if (state.exercisesPage + 1 >= state.exercisesTotalPages) {
    return;
  }
  state.exercisesPage += 1;
  try {
    await loadExercises();
    renderExercises();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.workoutForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  const exerciseIds = parseIdsFromForm(form, "exerciseIds");
  try {
    const payload = {
      name: data.get("name"),
      exerciseIds,
    };
    if (state.editing.workoutId) {
      await api(`/workouts/${state.editing.workoutId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    } else {
      await api("/workouts?withTransactional=true", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    }
    await refreshAllData();
    closeEntityModal();
    showNotice(state.editing.workoutId ? "Тренировка обновлена." : "Тренировка создана.", "success");
    resetEditingState();
    form.reset();
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.dayForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  try {
    const dateIso = elements.dayCalendarInput.value;
    if (!dateIso) {
      throw new Error("Выберите дату в календаре.");
    }
    const payload = {
      workoutId: Number(data.get("workoutId")),
      date: dateIso,
      calories: Number(data.get("calories")),
    };
    if (state.editing.dayDate) {
      if (dateIso === state.editing.dayDate) {
        await api(`/days/${state.editing.dayDate}`, {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });
      } else {
        await api("/days", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });
        if (state.editing.dayId) {
          await api(`/days/${state.editing.dayId}`, { method: "DELETE" });
        }
      }
    } else {
      await api("/days", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    }
    await refreshAllData();
    closeEntityModal();
    showNotice(state.editing.dayDate ? "Тренировочный день обновлен." : "Тренировочный день добавлен.", "success");
    resetEditingState();
    form.reset();
    form.elements.dateDisplay.readOnly = true;
    elements.dayCalendarInput.value = "";
  } catch (error) {
    showNotice(error.message, "error");
  }
});

elements.mediaForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = new FormData(form);
  const file = data.get("file");
  if (!file || !(file instanceof File) || file.size === 0) {
    showNotice("Выберите файл для загрузки.", "error");
    return;
  }

  try {
    const payload = new FormData();
    payload.append("file", file);
    const result = await api("/media", { method: "POST", body: payload });
    elements.mediaResult.textContent = `Загружено: ${result.filename}`;
    showNotice("Изображение загружено.", "success");
    form.reset();
    elements.mediaFileName.textContent = "Файл не выбран";
  } catch (error) {
    showNotice(error.message, "error");
  }
});

async function bootstrap() {
  setAuthView("login");
  setMainSection("categories");

  if (!getAccessToken()) {
    applyAuthState(false);
    return;
  }

  applyAuthState(true);
  try {
    await refreshAllData();
  } catch (error) {
    clearTokens();
    resetPrivateDataUI();
    applyAuthState(false);
    showNotice(error.message, "error", "auth", false);
  }
}

bootstrap();
