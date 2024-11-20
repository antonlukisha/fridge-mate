import { useMemo } from 'react';

export const useFilteredNotifications = (notifications, activeFilter) => {
  return useMemo(() => {
    return notifications.filter((notification) => {
      if (activeFilter === 'All') return true;
      return notification.type === activeFilter;
    });
  }, [notifications, activeFilter]);
};
