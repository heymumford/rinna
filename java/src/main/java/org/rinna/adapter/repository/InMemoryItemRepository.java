/*
 * Repository implementation for the Rinna workflow management system
 *
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * This file is subject to the terms and conditions defined in
 * the LICENSE file, which is part of this source code package.
 */

package org.rinna.adapter.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.rinna.domain.model.WorkItem;
import org.rinna.domain.model.WorkItemCreateRequest;
import org.rinna.domain.model.WorkItemRecord;
import org.rinna.domain.model.WorkItemType;
import org.rinna.domain.model.WorkflowState;
import org.rinna.repository.ItemRepository;

/**
 * In-memory implementation of the ItemRepository interface.
 * For testing and demonstration purposes only.
 */
public class InMemoryItemRepository implements ItemRepository {
    private final Map<UUID, WorkItem> items = new ConcurrentHashMap<>();
    
    @Override
    public WorkItem save(WorkItem item) {
        items.put(item.getId(), item);
        return item;
    }
    
    @Override
    public WorkItem create(WorkItemCreateRequest request) {
        WorkItem item = WorkItemRecord.fromRequest(UUID.randomUUID(), request);
        return save(item);
    }
    
    @Override
    public Optional<WorkItem> findById(UUID id) {
        return Optional.ofNullable(items.get(id));
    }
    
    @Override
    public List<WorkItem> findAll() {
        return new ArrayList<>(items.values());
    }
    
    @Override
    public List<WorkItem> findByType(String type) {
        try {
            WorkItemType workItemType = WorkItemType.valueOf(type.toUpperCase());
            return items.values().stream()
                    .filter(item -> item.getType() == workItemType)
                    .toList();
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<WorkItem> findByStatus(String status) {
        try {
            WorkflowState workflowState = WorkflowState.valueOf(status.toUpperCase());
            return items.values().stream()
                    .filter(item -> item.getStatus() == workflowState)
                    .toList();
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<WorkItem> findByAssignee(String assignee) {
        return items.values().stream()
                .filter(item -> Objects.equals(assignee, item.getAssignee()))
                .toList();
    }
    
    @Override
    public void deleteById(UUID id) {
        items.remove(id);
    }
    
    /**
     * Clears all items from the repository (for testing).
     */
    public void clear() {
        items.clear();
    }
}