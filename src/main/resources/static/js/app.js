/**
 * PetCareBook - Application Logic & REST Integration
 */

const API_BASE = '';
let allPets = [];
let allVaccineTypes = [];

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initDateConstraints();
    setupEventListeners();

    // Fetch initial datasets
    fetchDashboardStats();
    fetchDueSoon();
    fetchPets();
    fetchRecords();
    fetchVaccineTypes();
});

/* ========================================================
   1. NAVIGATION & TABS
   ======================================================== */
function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const tabPanes = document.querySelectorAll('.tab-pane');
    const pageTitle = document.getElementById('page-title');
    const pageDesc = document.getElementById('page-desc');

    const meta = {
        'tab-due-soon': { title: 'Vaccinations Due in Next 7 Days', desc: 'Automated schedule alerts protecting pets from missed immunization windows.' },
        'tab-pets': { title: 'Registered Pets Directory', desc: 'Browse and register pet profiles, breeds, age, and assigned owners.' },
        'tab-records': { title: 'Complete Vaccination Registry', desc: 'Chronological log of all administered vaccines, batch numbers, and clinics.' },
        'tab-history': { title: 'Pet Vaccination History Passport', desc: 'Inspect individual pet immunization history and countdown timelines.' },
        'tab-protocols': { title: 'Standard Vaccine Protocols', desc: 'Fixed intervals configured for automatic next due date calculations.' }
    };

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const target = item.getAttribute('data-tab');
            navItems.forEach(n => n.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));

            item.classList.add('active');
            const targetPane = document.getElementById(target);
            if (targetPane) targetPane.classList.add('active');

            if (meta[target]) {
                pageTitle.textContent = meta[target].title;
                pageDesc.textContent = meta[target].desc;
            }
        });
    });
}

function initDateConstraints() {
    const today = new Date().toISOString().split('T')[0];
    const vaxDateInput = document.getElementById('vax-date-given');
    const petDobInput = document.getElementById('pet-dob');

    // Enforce Business Rule 1 in UI: Max date is today
    if (vaxDateInput) {
        vaxDateInput.max = today;
        vaxDateInput.value = today;
    }
    if (petDobInput) {
        petDobInput.max = today;
    }
}

/* ========================================================
   2. DATA FETCHING & RENDERING
   ======================================================== */
async function fetchDashboardStats() {
    try {
        const res = await fetch(`${API_BASE}/api/dashboard/stats`);
        if (!res.ok) throw new Error('Stats fetch error');
        const data = await res.json();

        document.getElementById('stat-total-pets').textContent = data.totalPets || 0;
        document.getElementById('stat-due-soon').textContent = data.vaccinationsDueIn7Days || 0;
        document.getElementById('stat-overdue').textContent = data.overdueVaccinations || 0;
        document.getElementById('stat-total-vax').textContent = data.totalVaccinationsAdministered || 0;

        document.getElementById('sidebar-due-badge').textContent = data.vaccinationsDueIn7Days || 0;
        document.getElementById('due-count-pill').textContent = data.vaccinationsDueIn7Days || 0;
    } catch (err) {
        console.error('Error loading stats:', err);
    }
}

// Core Feature 4: List all pets with vaccinations due in the next 7 days
async function fetchDueSoon() {
    try {
        const res = await fetch(`${API_BASE}/api/vaccinations/due-soon?days=7`);
        if (!res.ok) throw new Error('Failed to fetch due soon records');
        const list = await res.json();

        const tbody = document.getElementById('due-soon-tbody');
        if (list.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted" style="padding: 24px;">No vaccinations due in the next 7 days. All pets are up to date!</td></tr>`;
            return;
        }

        tbody.innerHTML = list.map(item => `
            <tr>
                <td><strong>${item.petName}</strong></td>
                <td><span class="pet-species-badge">${item.species}</span> ${item.breed}</td>
                <td><span class="pill pill-warning"><i class="fa-solid fa-syringe"></i> ${item.vaccineName}</span></td>
                <td><strong>${item.nextDueDate}</strong></td>
                <td><span class="status-badge due-soon">${item.daysRemaining} days left</span></td>
                <td>${item.ownerName}<br><small class="text-muted">${item.ownerPhone}</small></td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="openVaccinationModalForPet(${item.petId}, ${item.vaccineTypeId})">
                        <i class="fa-solid fa-check"></i> Log Booster
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        console.error('Error fetching due soon:', err);
    }
}

// Core Feature 1: Pet Registry & Search
async function fetchPets() {
    try {
        const res = await fetch(`${API_BASE}/api/pets`);
        if (!res.ok) throw new Error('Failed to fetch pets');
        allPets = await res.json();

        renderPetsCards(allPets);
        populatePetDropdowns(allPets);
    } catch (err) {
        console.error('Error fetching pets:', err);
    }
}

function renderPetsCards(pets) {
    const container = document.getElementById('pets-cards-container');
    if (pets.length === 0) {
        container.innerHTML = `<div class="text-center text-muted" style="grid-column: 1/-1; padding: 40px 0;">No pets registered matching criteria.</div>`;
        return;
    }

    container.innerHTML = pets.map(p => {
        const age = calculateAge(p.dateOfBirth);
        return `
            <div class="pet-card">
                <div class="pet-card-top">
                    <span class="pet-name-header">${p.name}</span>
                    <span class="pet-species-badge">${p.species}</span>
                </div>
                <div class="pet-info-line"><strong>Breed:</strong> ${p.breed}</div>
                <div class="pet-info-line"><strong>Age:</strong> ${age} (${p.dateOfBirth})</div>
                <div class="pet-info-line"><strong>Owner:</strong> ${p.owner ? p.owner.fullName : 'None'}</div>
                <div class="pet-card-footer">
                    <button class="btn btn-sm btn-outline" onclick="viewPetHistory(${p.id})">
                        <i class="fa-solid fa-clipboard-list"></i> View History
                    </button>
                    <button class="btn btn-sm btn-primary" onclick="openVaccinationModalForPet(${p.id})">
                        <i class="fa-solid fa-syringe"></i> Log Vax
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function populatePetDropdowns(pets) {
    const vaxPetSelect = document.getElementById('vax-pet-select');
    const historyPetSelect = document.getElementById('history-pet-select');

    const options = pets.map(p => `<option value="${p.id}">${p.name} (${p.species} - ${p.breed})</option>`).join('');
    vaxPetSelect.innerHTML = '<option value="">-- Choose Pet --</option>' + options;
    historyPetSelect.innerHTML = '<option value="">-- Choose Pet --</option>' + options;
}

async function fetchRecords() {
    try {
        const res = await fetch(`${API_BASE}/api/vaccinations`);
        if (!res.ok) throw new Error('Failed to fetch records');
        const records = await res.json();

        const tbody = document.getElementById('records-tbody');
        if (records.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted">No vaccination records logged yet.</td></tr>`;
            return;
        }

        // Sort descending by ID
        const sorted = [...records].reverse();
        tbody.innerHTML = sorted.map(r => {
            let statusBadge = `<span class="status-badge up-to-date">Up to Date</span>`;
            if (r.status === 'OVERDUE') statusBadge = `<span class="status-badge overdue">Overdue</span>`;
            if (r.status === 'DUE_SOON') statusBadge = `<span class="status-badge due-soon">Due in ${r.daysRemaining}d</span>`;

            return `
                <tr>
                    <td><strong>${r.petName}</strong></td>
                    <td>${r.vaccineName}</td>
                    <td>${r.dateAdministered}</td>
                    <td>${r.standardIntervalDays} days</td>
                    <td><strong>${r.nextDueDate}</strong></td>
                    <td>${statusBadge}</td>
                    <td>${r.clinicOrVetName || '-'}</td>
                    <td><code>${r.batchNumber || '-'}</code></td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        console.error('Error fetching records:', err);
    }
}

async function fetchVaccineTypes() {
    try {
        const res = await fetch(`${API_BASE}/api/vaccine-types`);
        if (!res.ok) throw new Error('Failed to fetch vaccine types');
        allVaccineTypes = await res.json();

        // Populate modal select
        const vaxTypeSelect = document.getElementById('vax-type-select');
        vaxTypeSelect.innerHTML = '<option value="">-- Choose Vaccine --</option>' +
            allVaccineTypes.map(vt => `<option value="${vt.id}" data-interval="${vt.standardIntervalDays}">${vt.name} (${vt.targetSpecies}, Every ${vt.standardIntervalDays}d)</option>`).join('');

        // Populate protocols grid
        const grid = document.getElementById('protocols-grid');
        grid.innerHTML = allVaccineTypes.map(vt => `
            <div class="protocol-card">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <span class="pet-species-badge">${vt.targetSpecies}</span>
                    <span style="font-size:11px; font-weight:700; color:var(--primary);">${vt.isMandatory ? 'Mandatory Core' : 'Optional'}</span>
                </div>
                <h3 style="margin-top:10px; font-size:15px;">${vt.name}</h3>
                <div class="protocol-interval">${vt.standardIntervalDays} Days</div>
                <p style="font-size:12px; color:var(--text-secondary); line-height:1.5;">${vt.description || 'Standard preventive veterinary immunization protocol.'}</p>
            </div>
        `).join('');
    } catch (err) {
        console.error('Error fetching vaccine protocols:', err);
    }
}

// Core Feature 5: View a pet's complete vaccination history
async function viewPetHistory(petId) {
    document.querySelector('[data-tab="tab-history"]').click();
    document.getElementById('history-pet-select').value = petId;
    loadPetHistoryTimeline(petId);
}

async function loadPetHistoryTimeline(petId) {
    if (!petId) {
        document.getElementById('history-pet-overview').classList.add('hidden');
        document.getElementById('history-timeline-container').innerHTML = `
            <div class="text-center text-muted" style="padding: 40px 0;">
                Please select a pet from the dropdown above to view their vaccination passport.
            </div>
        `;
        return;
    }

    try {
        const pet = allPets.find(p => p.id == petId);
        const overview = document.getElementById('history-pet-overview');
        overview.classList.remove('hidden');
        overview.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:center; background:#f0fdf4; border:1px solid #bbf7d0; border-radius:10px; padding:16px 20px; margin-bottom:20px;">
                <div>
                    <h3 style="font-size:18px; color:#166534;"><i class="fa-solid fa-paw"></i> ${pet.name}'s Medical Passport</h3>
                    <div style="font-size:12px; color:#15803d; margin-top:4px;">
                        ${pet.species} | ${pet.breed} | Age: ${calculateAge(pet.dateOfBirth)} | Owner: ${pet.owner ? pet.owner.fullName : 'N/A'}
                    </div>
                </div>
                <button class="btn btn-sm btn-primary" onclick="openVaccinationModalForPet(${pet.id})">
                    <i class="fa-solid fa-plus"></i> Add Vaccination
                </button>
            </div>
        `;

        const res = await fetch(`${API_BASE}/api/vaccinations/pet/${petId}`);
        if (!res.ok) throw new Error('Failed to fetch history');
        const history = await res.json();

        const container = document.getElementById('history-timeline-container');
        if (history.length === 0) {
            container.innerHTML = `<div class="text-center text-muted" style="padding: 40px 0;">No vaccination history found for ${pet.name}.</div>`;
            return;
        }

        container.innerHTML = history.map(item => {
            let statusBadge = `<span class="status-badge up-to-date">Up to Date</span>`;
            if (item.status === 'OVERDUE') statusBadge = `<span class="status-badge overdue">Booster Overdue</span>`;
            if (item.status === 'DUE_SOON') statusBadge = `<span class="status-badge due-soon">Due in ${item.daysRemaining} days</span>`;

            return `
                <div class="timeline-item">
                    <div class="timeline-icon"><i class="fa-solid fa-syringe"></i></div>
                    <div class="timeline-content">
                        <div class="timeline-header">
                            <div>
                                <strong style="font-size:15px;">${item.vaccineName}</strong>
                                <span style="font-size:12px; color:var(--text-muted); margin-left:8px;">Administered: ${item.dateAdministered}</span>
                            </div>
                            ${statusBadge}
                        </div>
                        <div style="font-size:13px; color:var(--text-secondary); margin-bottom:6px;">
                            <strong>Next Due Date:</strong> ${item.nextDueDate} (Every ${item.standardIntervalDays} days)
                        </div>
                        <div style="font-size:12px; color:var(--text-muted);">
                            Clinic: <strong>${item.clinicOrVetName || 'Local Vet Clinic'}</strong> | Batch: <code>${item.batchNumber || 'N/A'}</code>
                        </div>
                        ${item.notes ? `<div style="font-size:12px; background:white; padding:8px 12px; border-radius:6px; margin-top:8px; border:1px solid var(--border-color);">${item.notes}</div>` : ''}
                    </div>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('Error loading history:', err);
    }
}

/* ========================================================
   3. EVENT LISTENERS & MODALS
   ======================================================== */
function setupEventListeners() {
    // Open Pet Modal
    document.getElementById('btn-open-pet-modal').addEventListener('click', () => {
        document.getElementById('modal-pet').classList.remove('hidden');
    });

    // Open Vaccination Modal
    document.getElementById('btn-open-vax-modal').addEventListener('click', () => {
        openVaccinationModal();
    });

    // Search Pets input
    document.getElementById('pet-search-input').addEventListener('input', triggerPetFilter);
    document.getElementById('pet-species-filter').addEventListener('change', triggerPetFilter);

    // History select change
    document.getElementById('history-pet-select').addEventListener('change', (e) => {
        loadPetHistoryTimeline(e.target.value);
    });

    // Auto calculation preview when Date or Vaccine Type changes (Core Feature 3)
    document.getElementById('vax-type-select').addEventListener('change', updateAutoCalculationPreview);
    document.getElementById('vax-date-given').addEventListener('input', updateAutoCalculationPreview);

    // Register Pet Form Submit
    document.getElementById('form-register-pet').addEventListener('submit', async (e) => {
        e.preventDefault();
        await handleRegisterPet();
    });

    // Log Vaccination Form Submit
    document.getElementById('form-log-vax').addEventListener('submit', async (e) => {
        e.preventDefault();
        await handleLogVaccination();
    });
}

function updateAutoCalculationPreview() {
    const typeSelect = document.getElementById('vax-type-select');
    const dateInput = document.getElementById('vax-date-given');
    const intervalText = document.getElementById('calc-interval-text');
    const dueText = document.getElementById('calc-due-text');

    const selectedOption = typeSelect.selectedOptions[0];
    const intervalDays = selectedOption ? parseInt(selectedOption.getAttribute('data-interval')) : null;
    const dateGivenVal = dateInput.value;

    if (intervalDays && dateGivenVal) {
        const givenDate = new Date(dateGivenVal);
        const calculatedDueDate = new Date(givenDate);
        calculatedDueDate.setDate(calculatedDueDate.getDate() + intervalDays);

        intervalText.textContent = `${intervalDays} Days`;
        dueText.textContent = calculatedDueDate.toISOString().split('T')[0];
    } else {
        intervalText.textContent = '-';
        dueText.textContent = '-';
    }
}

async function handleRegisterPet() {
    const payload = {
        name: document.getElementById('pet-name').value.trim(),
        species: document.getElementById('pet-species').value,
        breed: document.getElementById('pet-breed').value.trim(),
        dateOfBirth: document.getElementById('pet-dob').value,
        gender: document.getElementById('pet-gender').value,
        weightKg: parseFloat(document.getElementById('pet-weight').value) || null,
        ownerName: document.getElementById('pet-owner-name').value.trim(),
        ownerPhone: document.getElementById('pet-owner-phone').value.trim(),
        ownerEmail: document.getElementById('pet-owner-email').value.trim() || null
    };

    try {
        const res = await fetch(`${API_BASE}/api/pets`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errData = await res.json();
            throw new Error(errData.message || 'Registration failed');
        }

        closePetModal();
        document.getElementById('form-register-pet').reset();
        await fetchPets();
        fetchDashboardStats();
        alert('Pet successfully registered in PetCareBook!');
    } catch (err) {
        alert('Error: ' + err.message);
    }
}

async function handleLogVaccination() {
    const dateGiven = document.getElementById('vax-date-given').value;
    const today = new Date().toISOString().split('T')[0];

    // Enforce Business Rule 1 before sending
    if (dateGiven > today) {
        alert(`Validation Error: A vaccination record cannot be logged with a future date! Given: ${dateGiven}, Today: ${today}`);
        return;
    }

    const payload = {
        petId: parseInt(document.getElementById('vax-pet-select').value),
        vaccineTypeId: parseInt(document.getElementById('vax-type-select').value),
        dateAdministered: dateGiven,
        batchNumber: document.getElementById('vax-batch').value.trim() || null,
        clinicOrVetName: document.getElementById('vax-clinic').value.trim() || null,
        notes: document.getElementById('vax-notes').value.trim() || null
    };

    try {
        const res = await fetch(`${API_BASE}/api/vaccinations`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errData = await res.json();
            throw new Error(errData.message || 'Logging failed');
        }

        const data = await res.json();
        closeVaccinationModal();
        document.getElementById('form-log-vax').reset();

        // Refresh all records
        await Promise.all([
            fetchDueSoon(),
            fetchRecords(),
            fetchDashboardStats()
        ]);

        alert(`Vaccination logged successfully!\nNext Due Date calculated: ${data.nextDueDate} (+${data.standardIntervalDays} days)`);
    } catch (err) {
        alert('Error: ' + err.message);
    }
}

function openVaccinationModal() {
    document.getElementById('modal-vaccination').classList.remove('hidden');
    updateAutoCalculationPreview();
}

function openVaccinationModalForPet(petId, vaccineTypeId = null) {
    openVaccinationModal();
    document.getElementById('vax-pet-select').value = petId;
    if (vaccineTypeId) {
        document.getElementById('vax-type-select').value = vaccineTypeId;
    }
    updateAutoCalculationPreview();
}

function closePetModal() {
    document.getElementById('modal-pet').classList.add('hidden');
}

function closeVaccinationModal() {
    document.getElementById('modal-vaccination').classList.add('hidden');
}

function triggerPetFilter() {
    const kw = document.getElementById('pet-search-input').value.toLowerCase();
    const species = document.getElementById('pet-species-filter').value;

    const filtered = allPets.filter(p => {
        const matchesKw = !kw || p.name.toLowerCase().includes(kw) || p.breed.toLowerCase().includes(kw);
        const matchesSp = species === 'ALL' || p.species.toUpperCase() === species;
        return matchesKw && matchesSp;
    });

    renderPetsCards(filtered);
}

function calculateAge(dobStr) {
    if (!dobStr) return 'N/A';
    const dob = new Date(dobStr);
    const now = new Date();
    const diffYears = now.getFullYear() - dob.getFullYear();
    const diffMonths = now.getMonth() - dob.getMonth();

    if (diffYears > 0) {
        return `${diffYears} yr${diffYears > 1 ? 's' : ''}`;
    }
    const totalMonths = Math.max(1, diffMonths + (12 * diffYears));
    return `${totalMonths} mo${totalMonths > 1 ? 's' : ''}`;
}
