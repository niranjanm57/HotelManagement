function show(id) {
  document.querySelectorAll('.panel').forEach(p => p.classList.add('hidden'));
  document.getElementById(id).classList.remove('hidden');
}

document.getElementById('checkinForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const fd = new FormData(e.target);
  // build JSON with snake_case keys (we set jackson to SNAKE_CASE)
  const body = {
    guest_name: fd.get('guest_name'),
    room_no: Number(fd.get('room_no')),
    contact: fd.get('contact'),
    room_type: fd.get('room_type'),
    price_per_night: Number(fd.get('price_per_night')),
    check_out: fd.get('check_out')
  };

  try {
    const res = await fetch('/api/checkin', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(body)
    });

    const text = await res.text();
    const msgEl = document.getElementById('checkinMsg');
    if (!res.ok) {
      msgEl.textContent = 'Error: ' + text;
      msgEl.style.color = 'red';
    } else {
      msgEl.textContent = 'Success!';
      msgEl.style.color = 'green';
      e.target.reset();
    }
    loadReservations(); // refresh list
  } catch (err) {
    document.getElementById('checkinMsg').textContent = err.message;
  }
});

async function loadReservations() {
  show('reservations');
  const tbody = document.querySelector('#tableReservations tbody');
  tbody.innerHTML = '<tr><td colspan="9">Loading...</td></tr>';
  try {
    const res = await fetch('/api/reservations');
    const data = await res.json();
    if (!Array.isArray(data) || data.length === 0) {
      tbody.innerHTML = '<tr><td colspan="9">No reservations</td></tr>';
      return;
    }
    tbody.innerHTML = '';
    data.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${r.reservation_id}</td>
        <td>${r.guest_name}</td>
        <td>${r.room_no}</td>
        <td>${r.room_type}</td>
        <td>₹${r.price_per_night}</td>
        <td>${r.contact}</td>
        <td>${r.check_in}</td>
        <td>${r.check_out}</td>
        <td>
          <button class="action edit" onclick="editReservation(${r.reservation_id})">Edit</button>
          <button class="action checkout" onclick="checkout(${r.reservation_id})">Checkout</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="9">Error: ${err.message}</td></tr>`;
  }
}

async function checkout(id) {
  if (!confirm('Confirm checkout?')) return;
  try {
    const res = await fetch(`/api/checkout/${id}`, { method: 'DELETE' });
    const text = await res.text();
    alert(text);
    loadReservations();
  } catch (err) {
    alert('Error: ' + err.message);
  }
}

async function editReservation(id) {
  // Very simple inline-edit using prompts (for brevity).
  const guest = prompt('New guest name:');
  if (guest === null) return;
  const room = prompt('New room number:');
  if (room === null) return;
  const contact = prompt('New contact (10 digits):');
  if (contact === null) return;
  const body = { guest_name: guest, room_no: Number(room), contact: contact };
  try {
    const res = await fetch(`/api/reservation/${id}`, {
      method: 'PUT',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(body)
    });
    const txt = await res.text();
    if (!res.ok) alert('Error: ' + txt);
    else alert('Updated');
    loadReservations();
  } catch (err) {
    alert(err.message);
  }
}

async function searchByGuest() {
  const name = document.getElementById('guestSearchInput').value.trim();
  if (!name) return alert('Enter guest name');
  const res = await fetch(`/api/search/guest?name=${encodeURIComponent(name)}`);
  const data = await res.json();
  displaySearchResults(data, 'guestResults');
}

async function searchByDate() {
  const date = document.getElementById('dateSearchInput').value;
  if (!date) return alert('Pick a date');
  const res = await fetch(`/api/search/date?date=${encodeURIComponent(date)}`);
  const data = await res.json();
  displaySearchResults(data, 'dateResults');
}

function displaySearchResults(data, containerId) {
  const container = document.getElementById(containerId);
  if (!Array.isArray(data) || data.length === 0) {
    container.innerHTML = '<p>No results</p>';
    return;
  }
  let html = '<table><thead><tr><th>ID</th><th>Guest</th><th>Room</th><th>Check-In</th><th>Check-Out</th></tr></thead><tbody>';
  data.forEach(r => {
    html += `<tr><td>${r.reservation_id}</td><td>${r.guest_name}</td><td>${r.room_no}</td><td>${r.check_in}</td><td>${r.check_out}</td></tr>`;
  });
  html += '</tbody></table>';
  container.innerHTML = html;
}

// initial UI
show('checkin');
