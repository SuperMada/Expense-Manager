-- change all names with following: John Smith, Gabe Newell, Jane Doe, Peter Parker, George Brown, Bob Lee

PRAGMA foreign_keys = ON;

BEGIN;

INSERT INTO users (id, username, password, role, full_name) VALUES
  (1, 'mgr_john',   'Passw0rd!', 'manager',  'John Smith'),
  (2, 'mgr_gabe',   'Passw0rd!', 'manager',  'Gabe Newell'),
  (3, 'emp_jane',   'Passw0rd!', 'employee', 'Jane Doe'),
  (4, 'emp_peter',  'Passw0rd!', 'employee', 'Peter Parker'),
  (5, 'emp_george', 'Passw0rd!', 'employee', 'George Brown'),
  (6, 'emp_bob',    'Passw0rd!', 'employee', 'Bob Lee');

INSERT INTO categories (id, name) VALUES
  (1, 'Travel'),
  (2, 'Meals'),
  (3, 'Lodging'),
  (4, 'Office Supplies'),
  (5, 'Training'),
  (6, 'Other');

INSERT INTO expenses (id, user_id, category_id, amount, description, expense_date, created_at) VALUES
  (1,  3, 1,   250.00, 'Flight to client site for Q1 kickoff',           date('now','-5 days'),  datetime('now','-5 days')),
  (2,  3, 2,    45.50, 'Team lunch with client',                         date('now','-3 days'),  datetime('now','-3 days')),
  (3,  3, 3,   300.00, 'Hotel for client visit',                         date('now','-10 days'), datetime('now','-10 days')),
  (4,  3, 4,     0.01, 'Paper clip refill',                              date('now','-1 days'),  datetime('now','-1 days')),
  (5,  3, 5, 10000.00, 'Enterprise training package',                    date('now','-20 days'), datetime('now','-20 days')),
  (6,  3, 6,    12.99, 'Misc reimbursement',                             date('now','0 days'),   datetime('now','0 days')),
  (7,  3, 2,    78.25, 'Dinner with team',                               date('now','-15 days'), datetime('now','-15 days')),
  (8,  4, 1,   500.00, 'Conference travel to regional summit',           date('now','-6 days'),  datetime('now','-6 days')),
  (9,  4, 2,   200.00, 'Client dinner',                                  date('now','-12 days'), datetime('now','-12 days')),
  (10, 4, 4,    33.33, 'Notebooks and pens',                             date('now','-2 days'),  datetime('now','-2 days')),
  (11, 4, 3,  1200.00, 'Extended stay for project launch',               date('now','-30 days'), datetime('now','-30 days')),
  (12, 4, 5,   250.00, 'Online course subscription',                     date('now','-7 days'),  datetime('now','-7 days')),
  (13, 4, 6,    15.00, 'Parking fee',                                    date('now','-40 days'), datetime('now','-40 days')),
  (14, 5, 1,   450.00, 'Round trip flight to HQ',                        date('now','-4 days'),  datetime('now','-4 days')),
  (15, 5, 2,    88.88, 'Working lunch with department',                  date('now','-25 days'), datetime('now','-25 days')),
  (16, 5, 4,    65.00, 'Ergonomic keyboard',                             date('now','-35 days'), datetime('now','-35 days')),
  (17, 5, 3,   750.00, 'Hotel for annual conference',                    date('now','-8 days'),  datetime('now','-8 days')),
  (18, 5, 5,  1999.99, 'Professional certification course',              date('now','-50 days'), datetime('now','-50 days')),
  (19, 5, 6,     5.00, 'Postage for client mailer',                      date('now','-6 days'),  datetime('now','-6 days')),
  (20, 6, 1,   320.50, 'Train ticket to branch office',                  date('now','-45 days'), datetime('now','-45 days')),
  (21, 6, 2,    22.00, 'Breakfast meeting',                              date('now','-22 days'), datetime('now','-22 days')),
  (22, 6, 4,   200.00, 'Standing desk mat and accessories',              date('now','-9 days'),  datetime('now','-9 days')),
  (23, 6, 3,   950.00, 'Three-night stay for client onsite',             date('now','-60 days'), datetime('now','-60 days')),
  (24, 6, 5,   100.00, 'Workshop registration fee',                      date('now','-11 days'), datetime('now','-11 days')),
  (25, 6, 6,    75.00, 'Quarterly training workshop materials for the product team including printed workbooks, facilitator guides, participant handouts, venue deposit, catering, and reference cards for attendees in Q1 2026.',
                                                                         date('now','-55 days'), datetime('now','-55 days'));

INSERT INTO approvals (id, expense_id, status, reviewer_id, comment, review_date) VALUES
  (1,  1,  'pending',  NULL, NULL,                                   NULL),
  (2,  2,  'approved', 1,    'Approved',                             date('now','-2 days')),
  (3,  3,  'denied',   2,    'Please use our preferred vendor',      date('now','-8 days')),
  (4,  4,  'pending',  NULL, NULL,                                   NULL),
  (5,  5,  'approved', 1,    'Large but approved',                   date('now','-15 days')),
  (6,  6,  'pending',  NULL, NULL,                                   NULL),
  (7,  7,  'approved', 2,    NULL,                                   date('now','-12 days')),
  (8,  8,  'approved', 1,    'Approved for regional summit',         date('now','-4 days')),
  (9,  9,  'denied',   2,    'Exceeds meal limit per policy',        date('now','-10 days')),
  (10, 10, 'pending',  NULL, NULL,                                   NULL),
  (11, 11, 'approved', 1,    'Approved',                             date('now','-28 days')),
  (12, 12, 'pending',  NULL, NULL,                                   NULL),
  (13, 13, 'denied',   2,    'Parking covered separately',           date('now','-38 days')),
  (14, 14, 'pending',  NULL, NULL,                                   NULL),
  (15, 15, 'approved', 1,    NULL,                                   date('now','-20 days')),
  (16, 16, 'denied',   2,    'Please submit through IT procurement', date('now','-30 days')),
  (17, 17, 'pending',  NULL, NULL,                                   NULL),
  (18, 18, 'approved', 1,    'Critical skill for role',              date('now','-45 days')),
  (19, 19, 'pending',  NULL, NULL,                                   NULL),
  (20, 20, 'approved', 2,    NULL,                                   date('now','-40 days')),
  (21, 21, 'denied',   1,    'No documentation provided',            date('now','-20 days')),
  (22, 22, 'pending',  NULL, NULL,                                   NULL),
  (23, 23, 'approved', 2,    'Approved per project budget',          date('now','-55 days')),
  (24, 24, 'pending',  NULL, NULL,                                   NULL),
  (25, 25, 'approved', 1,    'Educational materials approved',       date('now','-50 days'));

COMMIT;