import React, { useState, useEffect } from 'react';
import { Chart as ChartJS, LineElement, PointElement, LineController, CategoryScale, LinearScale, Title, Tooltip, Legend } from 'chart.js';
import { Line } from 'react-chartjs-2';
import Header from '../components/Header';

ChartJS.register( LineElement, PointElement, LineController, CategoryScale, LinearScale, Title, Tooltip, Legend );

const Budget = () => {
  const actualExpenses = [0, 32640, 69570, 89320, 140100, 160123];
  const pastExpenses = [0, 28640, 59570, 77720, 130200, 150103];
  const plannedExpenses = [0, 30000, 60000, 90000, 120000, 150000]
  const labels = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь'];
  const data = {
    labels: labels,
    datasets: [{
      data: plannedExpenses,
      fill: false,
      borderColor: '#41A5FF',
      backgroundColor: '#41A5FF',
      tension: 0.1,
    }, {
      data: actualExpenses,
      fill: false,
      borderColor: '#62912C',
      backgroundColor: '#62912C',
      tension: 0.1,
    }, {
      data: pastExpenses,
      fill: false,
      borderColor: '#9A55FF',
      backgroundColor: '#9A55FF',
      tension: 0.1,
    }, ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: { callback: (value) => ( (value >= 1000) ? (value / 1000) + 'к' : value ), font: { family: 'Manrope, sans-serif', size: 12, }, color: '#212529', },
      },
      x: {
        ticks: { callback: (value) => data.labels[value].substring(0, 3), font: { family: 'Manrope, sans-serif', size: 12, }, color: '#212529', },
      },
    },
  };

  const [monthlyBudget, setMonthlyBudget] = useState(0);
  const [expenses, setExpenses] = useState([]);
  const [newExpense, setNewExpense] = useState({ name: '', amount: '' });
  const [totalSpent, setTotalSpent] = useState(0);

  // Calculate the total spent whenever expenses change
  useEffect(() => {
    const total = expenses.reduce((sum, expense) => sum + parseFloat(expense.amount || 0), 0);
    setTotalSpent(total);
  }, [expenses]);

  // Calculate remaining budget
  const remainingBudget = monthlyBudget - totalSpent;

  // Handle input changes for a new expense
  const handleExpenseChange = (e) => {
    const { name, value } = e.target;
    setNewExpense((prev) => ({ ...prev, [name]: value }));
  };

  // Add a new expense
  const handleAddExpense = () => {
    if (newExpense.name && newExpense.amount && !isNaN(newExpense.amount)) {
      setExpenses([...expenses, newExpense]);
      setNewExpense({ name: '', amount: '' });
    }
  };

  // Remove an expense
  const handleRemoveExpense = (index) => {
    const updatedExpenses = expenses.filter((_, i) => i !== index);
    setExpenses(updatedExpenses);
  };

  // Reset the budget and expenses
  const handleReset = () => {
    setMonthlyBudget(0);
    setExpenses([]);
    setNewExpense({ name: '', amount: '' });
  };

  return (
    <div className="main">
      <Header
        name={"Ваш бюджет"}
      />
      <div className="container">
        <section className="chart">
          <h2>Статистика расходов</h2>
          <div className="divider"></div>
          <div className="container">
            <div className="box">
              <svg viewBox="0 0 2 2" xmlns="http://www.w3.org/2000/svg" height="10px" width="10px">
                <circle cx="50%" cy="50%" r="1" fill="#41A5FF"/>
              </svg>
              <h3>Расходы</h3>
            </div>

            <div className="box">
              <svg viewBox="0 0 2 2" xmlns="http://www.w3.org/2000/svg" height="10px" width="10px">
                <circle cx="50%" cy="50%" r="1" fill="#62912C"/>
              </svg>
              <h3>Запланированные</h3>
            </div>

            <div className="box">
              <svg viewBox="0 0 2 2" xmlns="http://www.w3.org/2000/svg" height="10px" width="10px">
                <circle cx="50%" cy="50%" r="1" fill="#9A55FF"/>
              </svg>
              <h3>Расходы прошлого года</h3>
            </div>

          </div>
          <Line data={data} options={options} />
        </section>
      </div>
    </div>
  );
};

export default Budget;
