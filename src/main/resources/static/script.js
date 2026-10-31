const modeSelect = document.querySelector('#mode');
const methodSelect = document.querySelector('#method');
const pointsBody = document.querySelector('#points-body');
const pointCount = document.querySelector('#point-count');
const selectAll = document.querySelector('#select-all');
const resultContent = document.querySelector('#results-content');
const targetInput = document.querySelector('#target-x');
const calculateButton = document.querySelector('#calculate');

const methods = {
  newton: ['Linear', 'Quadratic', 'Polynomial'],
  lagrange: ['Linear', 'Quadratic', 'Polynomial'],
  spline: ['Linear']
};

function addPoint(x = '', y = '', selected = false) {
  const row = document.createElement('tr');
  row.innerHTML = `<td class="check-col"><input class="point-check" type="checkbox" aria-label="Select data point"></td>
    <td><input class="point-value" type="number" step="any" placeholder="0.00" aria-label="X value"></td>
    <td><input class="point-value" type="number" step="any" placeholder="0.00" aria-label="Y value"></td>
    <td class="actions-col"><button class="delete-row" type="button" aria-label="Delete data point" title="Delete row">−</button></td>`;
  row.querySelectorAll('.point-value')[0].value = x;
  row.querySelectorAll('.point-value')[1].value = y;
  row.querySelector('.point-check').checked = selected;
  pointsBody.append(row);
  updateRows();
}

function updateRows() {
  const checkboxes = [...pointsBody.querySelectorAll('.point-check')];
  const selectedCount = checkboxes.filter(box => box.checked).length;
  pointCount.textContent = `${checkboxes.length} ${checkboxes.length === 1 ? 'point' : 'points'}`;
  selectAll.checked = checkboxes.length > 0 && selectedCount === checkboxes.length;
  selectAll.indeterminate = selectedCount > 0 && selectedCount < checkboxes.length;
  [...pointsBody.rows].forEach(row => row.classList.toggle('selected', row.querySelector('.point-check').checked));
}

function updateMethods() {
  methodSelect.replaceChildren(...methods[modeSelect.value].map(label => {
    const option = document.createElement('option');
    option.value = label.toLowerCase();
    option.textContent = label;
    return option;
  }));
  clearResults();
}

function clearResults() {
  resultContent.replaceChildren();
  resultContent.hidden = true;
}

function addResultSection(title) {
  const section = document.createElement('section');
  section.className = 'result-section';
  const heading = document.createElement('h3');
  heading.className = 'result-heading';
  heading.textContent = title;
  section.append(heading);
  resultContent.append(section);
  return section;
}

function addResultDivider() {
  const divider = document.createElement('div');
  divider.className = 'result-divider';
  divider.setAttribute('aria-hidden', 'true');
  resultContent.append(divider);
}

function mathText(value) {
  const span = document.createElement('span');
  span.textContent = value;
  return span;
}

function appendInlineResult(expression, value) {
  expression.append(document.createTextNode(' = '));
  const finalValue = mathText(formatNumber(value));
  finalValue.className = 'equation-final';
  expression.append(finalValue);
  return expression;
}

function mathSub(base, index) {
  const span = document.createElement('span');
  span.append(document.createTextNode(base));
  const sub = document.createElement('sub');
  sub.textContent = index;
  span.append(sub);
  return span;
}

function mathGroup(section) {
  const scroll = document.createElement('div');
  scroll.className = 'math-scroll';
  const group = document.createElement('div');
  group.className = 'equation-group';
  scroll.append(group);
  section.append(scroll);
  return group;
}

function addEquation(group, lhs, rhs, options = {}) {
  const left = document.createElement('span');
  left.className = `equation-cell equation-lhs${options.emphasis ? ' equation-final' : ''}`;
  if (lhs) left.append(lhs);
  const equals = document.createElement('span');
  equals.className = `equation-cell equation-equals${options.emphasis ? ' equation-final' : ''}`;
  equals.textContent = '=';
  const right = document.createElement('span');
  right.className = `equation-cell equation-rhs${options.emphasis ? ' equation-final' : ''}`;
  if (rhs) right.append(rhs);
  group.append(left, equals, right);
}

function mathProduct(factors) {
  const product = document.createElement('span');
  factors.forEach((factor, index) => {
    if (index) product.append(document.createTextNode(' '));
    product.append(factor);
  });
  return product;
}

function mathFraction(numerator, denominator) {
  const fraction = document.createElement('span');
  fraction.className = 'math-fraction';
  const top = document.createElement('span');
  top.className = 'fraction-numerator';
  top.append(numerator);
  const bottom = document.createElement('span');
  bottom.className = 'fraction-denominator';
  bottom.append(denominator);
  fraction.append(top, bottom);
  return fraction;
}

function polynomialTerms(coefficients, points, substituteX = null) {
  const terms = document.createElement('span');
  coefficients.forEach((coefficient, index) => {
    if (index) terms.append(document.createTextNode(' + '));
    terms.append(document.createTextNode(`(${formatNumber(coefficient)})`));
    for (let factor = 0; factor < index; factor++) {
      terms.append(document.createTextNode(`(${substituteX == null ? 'x' : formatNumber(substituteX)} − ${formatNumber(points[factor].x)})`));
    }
  });
  return terms;
}

function lagrangeBasisExpression(index, points, targetX) {
  const numerator = [];
  const denominator = [];
  points.forEach((point, otherIndex) => {
    if (otherIndex === index) return;
    numerator.push(mathText(`(${formatNumber(targetX)} − ${formatNumber(point.x)})`));
    denominator.push(mathText(`(${formatNumber(points[index].x)} − ${formatNumber(point.x)})`));
  });
  return mathFraction(mathProduct(numerator), mathProduct(denominator));
}

function lagrangeSymbolicSum(points) {
  const sum = document.createElement('span');
  points.forEach((point, index) => {
    if (index) sum.append(document.createTextNode(' + '));
    const functionValue = document.createElement('span');
    functionValue.append(document.createTextNode('f('), mathSub('x', index), document.createTextNode(')'));
    sum.append(mathSub('L', index), functionValue);
  });
  return sum;
}

function formatNumber(value) {
  return Number(value).toPrecision(10).replace(/(?:\.0+|(?:(\.\d*?)0+))(?=e|$)/, '$1').replace('e+', 'e');
}

function displayResults(result, points) {
  clearResults();
  if (result.mode === 'newton') {
    const coefficientRows = mathGroup(resultContent);
    coefficientRows.classList.add('equation-group-spaced');
    result.coefficients.forEach((value, index) => {
      addEquation(coefficientRows, mathSub('C', index), mathText(formatNumber(value)));
    });
    addResultDivider();

    const evaluated = addResultSection('Final Result');
    const evaluatedRows = mathGroup(evaluated);
    addEquation(evaluatedRows, mathText('f(x)'), polynomialTerms(result.coefficients, points));
    if (result.value != null) {
      addEquation(evaluatedRows, mathText(`f(${formatNumber(result.targetX)})`), appendInlineResult(polynomialTerms(result.coefficients, points, result.targetX), result.value));
    }
  } else if (result.mode === 'lagrange') {
    if (result.targetX != null) {
      const basisRows = mathGroup(resultContent);
      basisRows.classList.add('lagrange-basis-list');
      result.basisFunctions.forEach(basis => {
        if (basis.valueAtTarget != null) {
          const basisGroup = document.createElement('div');
          basisGroup.className = 'equation-group lagrange-basis-entry';
          basisRows.append(basisGroup);
          addEquation(basisGroup, mathSub('L', basis.index), lagrangeBasisExpression(basis.index, points, result.targetX));
          addEquation(basisGroup, null, mathText(formatNumber(basis.valueAtTarget)));
        }
      });
      addResultDivider();
    }

    const evaluated = addResultSection('Final Result');
    const evaluatedRows = mathGroup(evaluated);
    addEquation(evaluatedRows, mathText('f(x)'), lagrangeSymbolicSum(points));
    if (result.value != null) {
      const substituted = document.createElement('span');
      points.forEach((point, index) => {
        if (index) substituted.append(document.createTextNode(' + '));
        substituted.append(document.createTextNode(`(${formatNumber(result.basisFunctions[index].valueAtTarget)})(${formatNumber(point.y)})`));
      });
      addEquation(evaluatedRows, mathText(`f(${formatNumber(result.targetX)})`), appendInlineResult(substituted, result.value));
    }
  } else {
    const sortedPoints = [...points].sort((left, right) => left.x - right.x);
    let targetInterval = null;
    const splineFunctions = addResultSection('Spline Functions');
    const splineRows = mathGroup(splineFunctions);
    splineRows.classList.add('equation-group-spaced');
    result.intervals.forEach((interval, index) => {
      if (interval.containsTarget) targetInterval = interval;
      const leftPoint = sortedPoints.find(point => point.x === interval.xStart);
      const slope = formatNumber(interval.slope);
      const leftY = formatNumber(leftPoint.y);
      const leftX = formatNumber(interval.xStart);
      const functionRhs = document.createElement('span');
      functionRhs.append(document.createTextNode(`(${leftY}) + (${slope})(x − ${leftX})`));
      const intervalLabel = mathText(`${formatNumber(interval.xStart)} ≤ x ≤ ${formatNumber(interval.xEnd)}`);
      intervalLabel.className = 'spline-interval';
      functionRhs.append(intervalLabel);
      const functionLabel = mathSub('f', index + 1);
      functionLabel.append(document.createTextNode('(x)'));
      addEquation(splineRows, functionLabel, functionRhs);
    });
    if (targetInterval && result.targetX != null && result.value != null) {
      const leftPoint = sortedPoints.find(point => point.x === targetInterval.xStart);
      const targetText = formatNumber(result.targetX);
      const evaluated = addResultSection('Final Result');
      const evaluatedRows = mathGroup(evaluated);
      const substitution = mathText(`(${formatNumber(leftPoint.y)}) + (${formatNumber(targetInterval.slope)})(${targetText} − ${formatNumber(targetInterval.xStart)})`);
      addEquation(evaluatedRows, mathText(`f(${targetText})`), appendInlineResult(substitution, result.value));
    }
  }
  resultContent.hidden = false;
}

function showError(message) {
  clearResults();
  const error = document.createElement('p');
  error.className = 'result-error';
  error.setAttribute('role', 'alert');
  error.textContent = message;
  resultContent.append(error);
  resultContent.hidden = false;
}

async function calculate() {
  if (calculateButton.disabled) return;
  clearResults();
  const selectedRows = [...pointsBody.rows].filter(row => row.querySelector('.point-check').checked);
  const points = [];
  for (const row of selectedRows) {
    const inputs = row.querySelectorAll('.point-value');
    const x = inputs[0].value.trim();
    const y = inputs[1].value.trim();
    if (!x || !y || !Number.isFinite(Number(x)) || !Number.isFinite(Number(y))) {
      showError('Selected points need numeric X and Y values.');
      return;
    }
    points.push({ x: Number(x), y: Number(y) });
  }

  const targetText = targetInput.value.trim();
  if (targetInput.validity.badInput) {
    showError('Target X must be a numeric value.');
    return;
  }
  const targetX = targetText === '' ? null : Number(targetText);
  if (targetX != null && !Number.isFinite(targetX)) {
    showError('Target X must be a numeric value.');
    return;
  }

  calculateButton.disabled = true;
  calculateButton.setAttribute('aria-busy', 'true');
  try {
    const response = await fetch('/api/interpolation/calculate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ mode: modeSelect.value, method: methodSelect.value, points, targetX })
    });
    const contentType = response.headers.get('content-type') || '';
    if (!contentType.includes('application/json')) {
      showError(response.status === 404
        ? 'Calculator API is unavailable. Restart the Spring Boot app.'
        : 'The calculator service returned an unexpected response. Try again.');
      return;
    }
    const data = await response.json();
    if (!response.ok) {
      showError(data.message || 'Check your selected points and try again.');
      return;
    }
    displayResults(data, points);
  } catch (error) {
    showError('Could not reach the calculator service. Try again.');
  } finally {
    calculateButton.disabled = false;
    calculateButton.removeAttribute('aria-busy');
  }
}

modeSelect.addEventListener('change', updateMethods);
methodSelect.addEventListener('change', clearResults);
pointsBody.addEventListener('change', event => {
  if (event.target.matches('.point-check')) updateRows();
});
pointsBody.addEventListener('input', event => {
  if (event.target.matches('.point-value')) clearResults();
});
pointsBody.addEventListener('click', event => {
  const deleteButton = event.target.closest('.delete-row');
  if (!deleteButton) return;
  deleteButton.closest('tr').remove();
  updateRows();
  clearResults();
});
selectAll.addEventListener('change', () => {
  pointsBody.querySelectorAll('.point-check').forEach(box => { box.checked = selectAll.checked; });
  updateRows();
  clearResults();
});
document.querySelector('#add-row').addEventListener('click', () => {
  addPoint();
  clearResults();
});
calculateButton.addEventListener('click', calculate);
targetInput.addEventListener('input', () => {
  targetInput.setCustomValidity('');
  clearResults();
});

addPoint(0, 1, true);
addPoint(1, 2, true);
addPoint(2, 4, true);
updateMethods();
