/**
 * Contains utility functions used by multiple components.
 */

import type {Voting} from "@/stores/voting";

/** An enum representing the possible roles of a user. */
export enum Role { User = 'user', Admin = 'admin' }

/**
 * Converts the given role string to the corresponding Role enum value or undefined if the string is not a valid role.
 * @param roleString the role string to convert
 */
export function toRole(roleString: string | null): Role | null {
  const possiblyRole = roleString?.toLowerCase() as Role;
  return (possiblyRole && Object.values(Role).includes(possiblyRole)) ? possiblyRole : null;
}

/**
 * Formats the given date as a string with the format "dd MMM yy" w.r.t. italian timezone (e.g. "01 Jan 21")
 * @param date the date to format
 * @param yearsDigit the number of digits to use for the year (2 or 4)
 */
export function formatDate(date: Date, yearsDigit: '2-digit' | 'numeric' | undefined = '2-digit'): string {
  const options: Intl.DateTimeFormatOptions = {
    day: '2-digit',
    month: 'short',
    year: yearsDigit,
  };
  return new Intl.DateTimeFormat('it-IT', options).format(date).toString();
}

/**
 * Formats the given time as a string with the format "hh:mm a" w.r.t. italian timezone (e.g. "01:00 PM")
 * @param date the time date to format
 */
export function formatTime(date: Date) {
  const options: Intl.DateTimeFormatOptions = {
    hour: '2-digit',
    minute: '2-digit',
    hour12: true, // Use 12-hour clock with AM/PM
  };
  return new Intl.DateTimeFormat('it-IT', options).format(date).toString();
}

/**
 * Returns the entry of the given record with the highest value.
 * @param data the record to analyze.
 */
export function highestOf(data: Record<string, number>): { key: string, value: number } {
  let maxKey: string = '';
  let maxValue: number = 0;
  for (const [key, value] of Object.entries(data)) {
    if (value > maxValue) {
      maxKey = key;
      maxValue = value;
    }
  }
  return { key: maxKey, value: maxValue as number };
}

/**
 * Capitalizes the first letter of the given string.
 * @param str the string to capitalize.
 */
export function capitalizeFirstLetter(str: string) {
  if (str === '') {
    return str;
  }
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Returns the status of the given election w.r.t. the given date.
 * @param election the election to analyze.
 * @param now the date to use as reference.
 */
export function getStatus(election: Voting, now: number): string {
  if (now >= election.start.getTime() && now < election.end.getTime()) {
    return "open";
  } else if (now >= election.end.getTime()) {
    return "closed";
  } else {
    return "soon";
  }
}
