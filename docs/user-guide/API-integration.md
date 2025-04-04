# API & Webhook Integration

Rinna provides REST API and webhook capabilities for external integrations.

## RESTful API

### Authentication

Include API token in Authorization header:
```
Authorization: Bearer ri-dev-f72a159e4bdc
```

### Content Type
```
Content-Type: application/json
```

### Endpoints

#### Work Items
```
GET    /api/v1/workitems                # List work items
POST   /api/v1/workitems                # Create work item
GET    /api/v1/workitems/{id}           # Get work item
PUT    /api/v1/workitems/{id}           # Update work item
POST   /api/v1/workitems/{id}/transitions # Transition work item
```

#### Projects
```
GET    /api/v1/projects                 # List projects
POST   /api/v1/projects                 # Create project
GET    /api/v1/projects/{key}           # Get project
PUT    /api/v1/projects/{key}           # Update project
GET    /api/v1/projects/{key}/workitems # Get project work items
```

#### Releases
```
GET    /api/v1/releases                 # List releases
POST   /api/v1/releases                 # Create release
GET    /api/v1/releases/{id}            # Get release
PUT    /api/v1/releases/{id}            # Update release
GET    /api/v1/releases/{id}/workitems  # Get release work items
POST   /api/v1/releases/{id}/workitems  # Add work item to release
DELETE /api/v1/releases/{id}/workitems/{workItemId} # Remove from release
```

### Examples

#### Creating a Work Item
```bash
curl -X POST "https://your-rinna-instance/api/v1/workitems" \
  -H "Authorization: Bearer ri-dev-f72a159e4bdc" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement payment gateway",
    "description": "Add support for PayPal and Stripe",
    "type": "FEATURE",
    "priority": "HIGH",
    "projectId": "billing-system",
    "metadata": {
      "source": "product_roadmap",
      "estimated_points": "8"
    }
  }'
```

#### Transitioning a Work Item
```bash
curl -X POST "https://your-rinna-instance/api/v1/workitems/wi-123/transitions" \
  -H "Authorization: Bearer ri-dev-f72a159e4bdc" \
  -H "Content-Type: application/json" \
  -d '{
    "toState": "IN_DEV",
    "comment": "Starting implementation"
  }'
```

## Webhook Integration

### GitHub Integration

Webhook URL:
```
https://your-rinna-instance/api/v1/webhooks/github?project=your-project-key
```

Configuration:
1. Generate webhook secret in Rinna project settings
2. Add webhook in GitHub repository settings
3. Select events: pull requests, issues, workflows, push events

### Custom Webhook

```bash
curl -X POST "https://your-rinna-instance/api/v1/webhooks/custom?project=your-project-key" \
  -H "Authorization: Bearer ri-dev-f72a159e4bdc" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New feature request",
    "description": "Add PDF export",
    "type": "FEATURE",
    "priority": "MEDIUM",
    "metadata": {
      "source": "customer_portal",
      "customer_id": "cust-123"
    }
  }'
```

## Java Client

```java
// Initialize client
RinnaClient client = RinnaClient.builder()
    .apiUrl("https://your-rinna-instance/api")
    .apiToken("ri-dev-f72a159e4bdc")
    .build();

// Create work item
WorkItemCreateRequest request = new WorkItemCreateRequest.Builder()
    .title("Implement feature X")
    .type(WorkItemType.FEATURE)
    .priority(Priority.HIGH)
    .description("Add support for feature X")
    .build();

WorkItem workItem = client.workItems().create(request);

// Transition work item
client.workItems().transition(workItem.getId(), WorkflowState.IN_DEV, "Starting implementation");
```

## Go Client

```go
// Initialize client
client := rinna.NewClient("https://your-rinna-instance/api", "ri-dev-f72a159e4bdc")

// Create work item
workItem, err := client.CreateWorkItem(rinna.WorkItemCreateRequest{
    Title:       "Implement feature X",
    Type:        rinna.WorkItemTypeFeature,
    Priority:    rinna.PriorityHigh,
    Description: "Add support for feature X",
})

// Transition work item
err = client.TransitionWorkItem(workItem.ID, rinna.WorkflowStateInDev, "Starting implementation")
```

## Security Considerations
- Protect API tokens
- Use HTTPS
- Validate webhook signatures
- Apply IP restrictions

## Rate Limiting
- 100 requests/minute/token
- 1000 requests/hour/token
- Custom limits configurable