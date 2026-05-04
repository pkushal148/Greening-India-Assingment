INSERT INTO users (id, name, email, password_hash) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Test User', 'test@example.com', 
     '$2a$12$Pu9wQ4BfArJ7XFQ7Md6fgexN3PVMxfHF8fRgLbYyHNy7sVGqljIGe');

INSERT INTO projects (id, name, description, owner_id) VALUES
    ('550e8400-e29b-41d4-a716-446655440002', 'Website Redesign', 'Q2 project', 
     '550e8400-e29b-41d4-a716-446655440001');


INSERT INTO tasks (id, title, description, status, priority, project_id, assignee_id, due_date) VALUES
    ('550e8400-e29b-41d4-a716-446655440003', 'Design homepage', 'Create homepage mockup', 
     'todo', 'high', '550e8400-e29b-41d4-a716-446655440002', 
     '550e8400-e29b-41d4-a716-446655440001', '2026-04-30'),
    
    ('550e8400-e29b-41d4-a716-446655440004', 'Setup database', 'Configure PostgreSQL schema', 
     'in_progress', 'high', '550e8400-e29b-41d4-a716-446655440002', 
     '550e8400-e29b-41d4-a716-446655440001', '2026-04-25'),
    
    ('550e8400-e29b-41d4-a716-446655440005', 'Write API docs', 'Document all endpoints', 
     'done', 'medium', '550e8400-e29b-41d4-a716-446655440002', NULL, '2026-04-20');